package models.game.test

import models.game.gem.{ Color, Gem }

object GameTestCrash extends GameTest(seed = 0, width = 6, height = 12) {
  override def init() = {
    board.add(Gem(0, Color.Red), 0, 0)
    board.add(Gem(1, Color.Red, crash = true), 1, 0)
    board.add(Gem(2, Color.Red), 2, 0)
    board.add(Gem(3, Color.Red), 3, 0)
    board.add(Gem(4, Color.Red), 3, 1)
    board.add(Gem(5, Color.Blue), 0, 1)
    board.add(Gem(6, Color.Green), 3, 2)

    testBoard.add(Gem(5, Color.Blue), 0, 0)
    testBoard.add(Gem(6, Color.Green), 3, 0)
  }

  override def run() = {
    board.crash()
    board.collapse()
  }
}
