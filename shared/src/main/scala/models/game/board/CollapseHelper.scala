package models.game.board

import models.game.board.mutation.Mutation
import models.game.board.mutation.Mutation.MoveGem

trait CollapseHelper { this: Board =>
  def collapse(): Seq[Mutation] = mapGems { (gem, x, y) =>
    val moveIndexes = (0 until gem.width.getOrElse(1)).map { xOffset =>
      y match {
        case 0 => None
        case _ => ((y - 1) to 0 by -1).find(idx => at(x + xOffset, idx).isDefined) match {
          case Some(idx) if idx == y - 1 => None
          case Some(idx) => Some(idx + 1)
          case None => Some(0)
        }
      }
    }

    val moveIndex = if(moveIndexes.contains(None)) {
      None
    } else {
      Some(moveIndexes.flatten.max)
    }

    moveIndex.map(idx => applyMutation(MoveGem(x, y, 0, idx - y))).toSeq
  }.flatten
}
