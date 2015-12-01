package models.player

import models.board.mutation.Mutation.ActiveGemsUpdate
import models.board.mutation.UpdateSegment
import models.gem.GemLocation

trait ActiveGemHelper extends ActiveGemMoveHelper with ActiveGemRotationHelper { this: Player =>
  def activeGemsCreate() = {
    if (activeGems.nonEmpty) {
      throw new IllegalStateException(s"Active gems created, but player [$id] already has active gems [${activeGems.mkString(", ")}].")
    }
    val x = (board.width / 2) - 1
    val y = board.height - 1
    if (!board.isValid(x, y)) {
      throw new IllegalStateException(s"Cannot create active gems, as [$x, $y] is occupied by [${board.at(x, y)}].")
    }
    if (!board.isValid(x + 1, y)) {
      throw new IllegalStateException(s"Cannot create active gems, as [${x + 1}, $y] is occupied by [${board.at(x + 1, y)}].")
    }
    activeGems = Seq(GemLocation(gemStream.next, x, y), GemLocation(gemStream.next, x + 1, y))
    UpdateSegment(Seq(ActiveGemsUpdate(activeGems)))
  }

  def activeGemsDrop() = {
    val orderedGems = activeGems.sortBy(g => g.y -> g.x)
    activeGems = Seq.empty
    val msgs = orderedGems.flatMap(ag => board.drop(ag.gem, ag.x, ag.y))
    UpdateSegment(msgs)
  }
}
