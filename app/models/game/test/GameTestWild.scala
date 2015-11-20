package models.game.test

import models.game.gem.{ Color, Gem }

object GameTestWild extends GameTest.Provider {
  override def newInstance() = new GameTestWild()
}

class GameTestWild() extends GameTest() {
  override def init() = {
    test.board.add(Gem(0, Color.Red), 0, 0)
    test.board.add(Gem(1, Color.Red, width = Some(2), height = Some(2)), 1, 0)
    test.board.add(Gem(2, Color.Red, crash = true), 3, 0)
    test.board.add(Gem(3, Color.Red, timer = Some(1)), 4, 0)
    test.board.add(Gem(4, Color.Blue), 5, 0)
    test.board.add(Gem(5, Color.Wild), 1, 2)

    goal.board.add(Gem(2, Color.Red, crash = true), 3, 0)
    goal.board.add(Gem(3, Color.Red, timer = Some(1)), 4, 0)
    goal.board.add(Gem(4, Color.Blue), 5, 0)
  }

  override def run() = test.board.processWilds()
}
