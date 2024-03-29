package services.supervisor

import java.util.UUID

import akka.actor.SupervisorStrategy.Stop
import akka.actor._
import models._
import models.user.User
import org.joda.time.LocalDateTime
import utils.metrics.MetricsServletActor
import utils.{ ApplicationContext, DateUtils, Logging }

object ActorSupervisor extends Logging {
  final case class BrawlRecord(connections: Seq[(UUID, String)], actorRef: ActorRef, started: LocalDateTime)
  final case class ConnectionRecord(userId: UUID, name: String, actorRef: ActorRef, var activeBrawl: Option[UUID], started: LocalDateTime)
}

class ActorSupervisor(val ctx: ApplicationContext) extends ActorSupervisorHelper {
  import ActorSupervisor._

  protected[this] val connections = collection.mutable.HashMap.empty[UUID, ConnectionRecord]
  protected[this] val connectionsCounter = metrics.counter("active-connections")

  protected[this] val brawls = collection.mutable.HashMap.empty[UUID, BrawlRecord]
  protected[this] val brawlsCounter = metrics.counter("active-brawls")

  override def preStart() {
    context.actorOf(MetricsServletActor.props(ctx.config), "metrics-servlet")
  }

  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
    case _ => Stop
  }

  override def receiveRequest = {
    case cs: ConnectionStarted => timeReceive(cs) { handleConnectionStarted(cs.user, cs.connectionId, cs.conn) }
    case cs: ConnectionStopped => timeReceive(cs) { handleConnectionStopped(cs.connectionId) }

    case cb: CreateBrawl => timeReceive(cb) { handleCreateBrawl(cb.scenario, cb.connectionId, cb.seed) }
    case cbj: ConnectionBrawlJoin => timeReceive(cbj) { handleConnectionBrawlJoin(cbj.id, cbj.connectionId) }
    case cbo: ConnectionBrawlObserve => timeReceive(cbo) { handleConnectionBrawlObserve(cbo.id, cbo.connectionId, cbo.as) }
    case bs: BrawlStopped => timeReceive(bs) { handleBrawlStopped(bs.id) }

    case GetSystemStatus => timeReceive(GetSystemStatus) { handleGetSystemStatus() }
    case ct: SendConnectionTrace => timeReceive(ct) { handleSendConnectionTrace(ct) }
    case ct: SendClientTrace => timeReceive(ct) { handleSendClientTrace(ct) }
    case bt: SendBrawlTrace => timeReceive(bt) { handleSendBrawlTrace(bt) }

    case im: InternalMessage => log.warn(s"Unhandled internal message [${im.getClass.getSimpleName}] received.")
    case x => log.warn(s"ActorSupervisor encountered unknown message: ${x.toString}")
  }

  private[this] def handleGetSystemStatus() = {
    val brawlStatuses = brawls.toList.sortBy(_._1).map(x => x._1 -> x._2.connections)
    val connectionStatuses = connections.toList.sortBy(_._2.name).map(x => x._1 -> x._2.name)
    sender() ! SystemStatus(brawlStatuses, connectionStatuses)
  }

  private[this] def handleSendConnectionTrace(ct: SendConnectionTrace) = connections.find(_._1 == ct.id) match {
    case Some(c) => c._2.actorRef forward ct
    case None => sender() ! ServerError(s"Unknown Connection [${ct.id}].", ct.id.toString)
  }

  private[this] def handleSendClientTrace(ct: SendClientTrace) = connections.find(_._1 == ct.id) match {
    case Some(c) => c._2.actorRef forward ct
    case None => sender() ! ServerError(s"Unknown Client Connection [${ct.id}].", ct.id.toString)
  }

  private[this] def handleSendBrawlTrace(bt: SendBrawlTrace) = brawls.get(bt.id) match {
    case Some(g) => g.actorRef forward bt
    case None => sender() ! ServerError(s"Unknown Brawl [${bt.id}].", bt.id.toString)
  }

  protected[this] def handleConnectionStarted(user: User, connectionId: UUID, conn: ActorRef) {
    log.debug(s"Connection [$connectionId] registered to [${user.username.getOrElse(user.id)}] with path [${conn.path}].")
    connections(connectionId) = ConnectionRecord(user.id, user.username.getOrElse("Guest"), conn, None, DateUtils.now)
    connectionsCounter.inc()
  }

  protected[this] def handleConnectionStopped(id: UUID) {
    matchmaking.connectionStopped(id).foreach { x =>
      val requiredPlayers = matchmaking.getRequiredPlayerCount(x._1)
      val conns = x._2.map(p => connections.getOrElse(p, throw new IllegalArgumentException(s"Missing connection definition for [$p].")))
      val playerNames = conns.map(_.name)
      val msg = BrawlQueueUpdate(x._1, requiredPlayers, playerNames)
      conns.foreach(_.actorRef ! msg)
    }
    connections.remove(id) match {
      case Some(conn) =>
        connectionsCounter.dec()
        conn.activeBrawl.foreach { bId =>
          brawls(bId).actorRef ! ConnectionStopped(id)
        }
        log.debug(s"Connection [$id] [${conn.actorRef.path}] stopped.")
      case None => log.warn(s"Connection [$id] stopped but is not registered.")
    }
  }
}
