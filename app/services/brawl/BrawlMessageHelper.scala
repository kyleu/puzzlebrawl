package services.brawl

import models._
import models.board.mutation.Mutation.TargetChanged
import models.board.mutation.{ Mutation, UpdateSegment }
import utils.DateUtils

import scala.util.control.NonFatal

trait BrawlMessageHelper { this: BrawlService =>

  protected[this] def handleBrawlRequest(br: BrawlRequest) = {
    log.debug("Handling [" + br.message.getClass.getSimpleName.stripSuffix("$") + "] message from user [" + br.userId + "] for brawl [" + brawl.id + "].")
    try {
      val time = DateUtils.now
      brawlMessages.map(_ += ((br.message, br.userId, time)))
      val player = brawl.playersById(br.userId)

      def sendMove(m: Mutation, key: String = "active-move") = {
        player.board.incrementMoveCount(DateUtils.nowMillis)
        sendToAll(PlayerUpdate(player.id, Seq(UpdateSegment(key, Seq(m)))))
      }

      br.message match {
        case x if brawl.completed.isDefined => log.warn(s"Received brawl message [${x.getClass.getSimpleName}] for completed brawl [$brawl.id].")
        case ActiveGemsLeft => player.activeGemsLeft().foreach(m => sendMove(m))
        case ActiveGemsRight => player.activeGemsRight().foreach(m => sendMove(m))
        case ActiveGemsClockwise => player.activeGemsClockwise().foreach(m => sendMove(m))
        case ActiveGemsCounterClockwise => player.activeGemsCounterClockwise().foreach(m => sendMove(m))
        case ActiveGemsStep => player.activeGemsStep().foreach(m => sendToAll(PlayerUpdate(player.id, Seq(UpdateSegment("active-step", Seq(m))))))
        case ActiveGemsDrop =>
          player.board.incrementMoveCount(DateUtils.nowMillis)
          val messages = player.dropActiveFullTurn(brawl)
          sendToAll(PlayerUpdate(player.id, messages))

        case st: SelectTarget => if (!player.target.contains(st.target)) {
          player.target = Some(st.target)
          sendToAll(PlayerUpdate(player.id, Seq(UpdateSegment("target", Seq(TargetChanged(st.target))))))
        }

        case ResignBrawl => log.info(s"Player [${player.id}] has resigned from brawl [$id].")

        case r => log.warn(s"BrawlService received unknown brawl message [${r.getClass.getSimpleName.stripSuffix("$")}].")
      }
    } catch {
      case NonFatal(x) =>
        log.error(s"Exception processing brawl request [$br].", x)
        sender() ! ServerError(x.getClass.getSimpleName, x.getMessage)
    }
  }
}
