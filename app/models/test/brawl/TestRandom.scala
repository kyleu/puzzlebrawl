package models.test.brawl

import java.util.UUID

import models.board.mutation.Mutation.AddGem
import models.board.mutation.UpdateSegment

object TestRandom extends BrawlTest.Provider {
  override def newInstance(id: UUID, self: UUID) = new TestRandom(id, self)
}

class TestRandom(id: UUID, self: UUID) extends BrawlTest(id, self) {
  override def init() = {
    for (y <- 0 until goal.board.height) {
      for (x <- 0 until goal.board.width) {
        goal.board.applyMutation(AddGem(goal.gemStream.next(), x, y))
      }
    }
  }

  override def run() = Seq(UpdateSegment(
    "random",
    (0 until test.board.height).flatMap { y =>
      (0 until test.board.width).map { x =>
        test.board.applyMutation(AddGem(test.gemStream.next(), x, y))
      }
    }
  ))
}
