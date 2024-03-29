package models.test.brawl

import java.util.UUID

import models.board.mutation.Mutation.AddGem
import models.gem.Gem

object TestScratchpad extends BrawlTest.Provider {
  override def newInstance(id: UUID, self: UUID) = new TestScratchpad(id, self)
}

class TestScratchpad(id: UUID, self: UUID) extends BrawlTest(id, self) {
  override def init() = {
    test.board.applyMutation(AddGem(Gem(0, width = Some(2), height = Some(2)), 0, 0))
    test.board.applyMutation(AddGem(Gem(2), 2, 0))
    test.board.applyMutation(AddGem(Gem(3), 2, 1))
    test.board.applyMutation(AddGem(Gem(4, width = Some(2), height = Some(2)), 3, 0))

    goal.board.applyMutation(AddGem(Gem(0, width = Some(5), height = Some(2)), 0, 0))
  }

  override def run() = test.board.fuse()
}

