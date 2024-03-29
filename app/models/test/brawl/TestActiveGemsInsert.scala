package models.test.brawl

import java.util.UUID

import models.board.mutation.Mutation.AddGem
import models.gem.{ Color, Gem }

object TestActiveGemsInsert extends BrawlTest.Provider {
  override def newInstance(id: UUID, self: UUID) = new TestActiveGemsInsert(id, self)
}

class TestActiveGemsInsert(id: UUID, self: UUID) extends BrawlTest(id, self) {
  override def init() = {
    test.activeGemsCreate(Gem(1, color = Color.Blue), Gem(0, color = Color.Yellow))

    test.board.applyMutation(AddGem(Gem(2, width = Some(2), height = Some(3)), 0, 0))
    test.board.applyMutation(AddGem(Gem(3, color = Color.Blue, crash = Some(true)), 0, 3))
    test.board.applyMutation(AddGem(Gem(4, width = Some(2), height = Some(3)), 0, 4))

    goal.board.applyMutation(AddGem(Gem(2, width = Some(2), height = Some(6)), 0, 0))
    goal.board.applyMutation(AddGem(Gem(0, color = Color.Yellow), 2, 0))
    goal.score = 200
  }

  override def run() = {
    (0 until 8).foreach(x => test.activeGemsStep())
    test.activeGemsLeft()
    test.activeGemsDrop()
    val msgs = test.board.fullTurn()
    test.score += msgs.flatMap(_.scoreDelta).sum
    msgs
  }
}
