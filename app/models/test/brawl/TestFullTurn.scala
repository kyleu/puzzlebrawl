package models.test.brawl

import models.board.mutation.Mutation.AddGem

import scala.util.Random

object TestFullTurn extends Test.Provider {
  override def newInstance() = new TestFullTurn()
}

class TestFullTurn() extends Test(seed = Some(Random.nextInt())) {
  override def init() = {
    for (y <- 0 until test.board.height) {
      for (x <- 0 until test.board.width) {
        test.board.applyMutation(AddGem(test.gemStream.next, x, y))
      }
    }

    for (y <- 0 until goal.board.height) {
      for (x <- 0 until goal.board.width) {
        goal.board.applyMutation(AddGem(goal.gemStream.next, x, y))
      }
    }
    goal.board.fullTurn()
  }

  override def run() = {
    test.board.fullTurn()
  }
}