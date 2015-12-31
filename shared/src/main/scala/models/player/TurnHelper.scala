package models.player

import models.board.mutation.Mutation.AddGem
import models.board.mutation.UpdateSegment
import models.brawl.Brawl

trait TurnHelper { this: Player =>
  final def dropActiveFullTurn(brawl: Brawl) = {
    val dropSegment = Seq(activeGemsDrop())

    val timerSegment = board.decrementTimers().toSeq

    val wildSegment = board.processWilds()
    val combo = if (wildSegment.isEmpty) { 1 } else { 2 }
    val postWildSegment = if (wildSegment.isEmpty) { Seq.empty } else { board.collapse() ++ board.fuse() }

    val fullTurnSegments = board.fullTurn(combo = combo)

    val pendingSegment = processPendingGems()
    val postPendingSegment = if (pendingSegment.isEmpty) { Seq.empty } else { board.collapse() ++ board.fuse() }

    val activeCreate = if (board.at(2, board.height - 1).isDefined || board.at(3, board.height - 1).isDefined) {
      brawl.onLoss(id)
      Seq.empty
    } else {
      Seq(activeGemsCreate())
    }

    val messages = dropSegment ++ timerSegment ++ wildSegment ++ postWildSegment ++ fullTurnSegments ++ pendingSegment ++ postPendingSegment ++ activeCreate

    val scoreDelta = messages.flatMap(_.scoreDelta).sum
    score += scoreDelta

    val chargeDeltas = messages.flatMap(_.charge).filter(_ != 0.0)
    if (chargeDeltas.nonEmpty) {
      brawl.applyCharge(id, chargeDeltas)
    }

    messages
  }

  private[this] def processPendingGems() = if (pendingGems.isEmpty) {
    Seq.empty
  } else {
    val groupedGems = pendingGems.groupBy(_.x).map(x => x._1 -> x._2.map(_.gem).reverse.zipWithIndex)
    val additions = (0 until board.width).flatMap { i =>
      groupedGems.getOrElse(i, Seq.empty).map { gem =>
        this.board.applyMutation(AddGem(gem._1, i, board.height - gem._2 - 1))
      }
    }
    pendingGems = Seq.empty
    Seq(UpdateSegment("pending", additions))
  }
}
