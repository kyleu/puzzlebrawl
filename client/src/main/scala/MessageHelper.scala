import java.util.UUID

import json.{ BaseSerializers, BrawlSerializers, JsonUtils }
import models._
import models.board.mutation.UpdateSegment
import models.brawl.Brawl

import scala.scalajs.js
import scala.util.control.NonFatal

trait MessageHelper { this: PuzzleBrawl =>
  protected[this] def handleBlendMessage(c: String, v: js.Dynamic) = {
    c match {
      case _ =>
        this.socket.foreach(_.send(c, v))
      //handleOfflineMessage(c, v)
    }
  }

  protected[this] def handleOfflineMessage(c: String, v: js.Dynamic) = try {
    c match {
      case "GetVersion" => send(VersionResponse("0.0"))
      case "Ping" => send(Pong(JsonUtils.getLong(v.timestamp)))
      case "DebugInfo" => v.data.toString match {
        case "sync" => send(DebugResponse("sync", BaseSerializers.write(BrawlSerializers.write(brawl))))
        case x if x.startsWith("cheat-") => handleCheat(x.stripPrefix("cheat-"))
        case _ => throw new IllegalArgumentException(s"Unhandled debug request [${v.data.toString}].")
      }

      case "StartBrawl" => handleStartBrawl(v.scenario.toString)

      case "ActiveGemsLeft" => activePlayer.foreach(p => p.activeGemsLeft().foreach { m =>
        send(PlayerUpdate(p.id, Seq(UpdateSegment("active-move", Seq(m)))))
      })
      case "ActiveGemsRight" => activePlayer.foreach(p => p.activeGemsRight().foreach { m =>
        send(PlayerUpdate(p.id, Seq(UpdateSegment("active-move", Seq(m)))))
      })
      case "ActiveGemsClockwise" => activePlayer.foreach(p => p.activeGemsClockwise().foreach { m =>
        send(PlayerUpdate(p.id, Seq(UpdateSegment("active-move", Seq(m)))))
      })
      case "ActiveGemsCounterClockwise" => activePlayer.foreach(p => p.activeGemsCounterClockwise().foreach { m =>
        send(PlayerUpdate(p.id, Seq(UpdateSegment("active-move", Seq(m)))))
      })
      case "ActiveGemsStep" => activePlayer.foreach(p => p.activeGemsStep().foreach { m =>
        send(PlayerUpdate(p.id, Seq(UpdateSegment("active-step", Seq(m)))))
      })
      case "ActiveGemsDrop" => activePlayer.foreach { p =>
        send(PlayerUpdate(p.id, p.dropActiveFullTurn(brawl)))
      }

      case "ResignBrawl" => activePlayer.foreach { p =>
        if (v.id.toString != brawl.id.toString) {
          throw new IllegalStateException(s"Tried to resign brawl [${v.id}], but active brawl is [${brawl.id}].")
        }
        onLoss(p.id)
      }

      case _ => throw new IllegalStateException(s"Invalid message [$c].")
    }
  } catch {
    case NonFatal(x) => send(ServerError(x.getClass.getSimpleName, x.getMessage))
  }

  protected[this] def handleStartBrawl(scenario: String) = {
    if (scenario != "offline") {
      throw new IllegalStateException(s"Can't handle scenario [$scenario].")
    }
    val players = Seq(userId -> "Offline User")
    val brawl = Brawl.blank(UUID.randomUUID, "offline", players = players)
    brawl.setCallbacks(this)
    brawl.players.foreach(_.activeGemsCreate())
    activeBrawl = Some(brawl)
    activePlayer = brawl.players.find(p => p.id == userId)
    send(BrawlJoined(userId, brawl))
  }

  protected[this] def handleCheat(key: String) = key match {
    case "victory" => send(brawl.getCompletionReport)
    case _ => throw new IllegalStateException(s"Unknown cheat [$key].")
  }
}
