package services.brawl

import java.util.UUID

import akka.actor.Props
import models._
import models.scenario.Scenario
import models.user.PlayerRecord
import utils.{ Config, DateUtils }

object BrawlService {
  def props(id: UUID, scenario: String, players: Seq[PlayerRecord], seed: Int, notificationCallback: (String) => Unit) = {
    Props(classOf[BrawlService], id, scenario, players, seed, notificationCallback)
  }
}

final case class BrawlService(id: UUID, scenario: String, players: Seq[PlayerRecord], seed: Int, notificationCallback: (String) => Unit) extends BrawlHelper {
  protected[this] lazy val brawl = try {
    val b = Scenario.newInstance(id, scenario, seed, players)
    b.setCallbacks(this)
    b
  } catch {
    case x: IllegalArgumentException =>
      players.foreach { player =>
        player.connectionActor.foreach(_ ! ServerError("Invalid Scenario", x.getMessage))
      }
      throw x
  }

  protected[this] val playersById = players.map(x => x.userId -> x).toMap
  protected[this] val observerConnections = collection.mutable.ArrayBuffer.empty[(PlayerRecord, Option[UUID])]

  override def preStart() = {
    log.info(s"Starting brawl scenario [$scenario] for [${players.map(p => p.userId + ": " + p.name).mkString(", ")}] with seed [$seed].")

    val startMessage = BrawlStarted(brawl.id, self, DateUtils.fromMillis(brawl.started))
    players.foreach { player =>
      player.connectionActor.foreach(_ ! startMessage)
      player.connectionActor.foreach(_ ! BrawlJoined(player.userId, brawl))
    }

    insertHistory()

    val playersString = players.map(x => x.userId + " (" + x.name + ")").mkString(", ")
    val msg = s"Brawl `$id` started on `${Config.hostname}` with seed `$seed` using scenario `$scenario` for players `$playersString`."

    startSchedules()

    notificationCallback(msg)
  }

  override def postStop() = {
    val msg = s"Game completion report for `$id`:\n  " + players.map { p =>
      val bp = brawl.playersById(p.userId)
      s"`${p.userId}` (${p.name}): ${bp.board.getMoveCount} moves, ${bp.board.getNormalGemCount} normal gems, and ${bp.board.getTimerGemCount} timer gems."
    }.mkString("\n  ")
    notificationCallback(msg)
  }
}
