package models.game.test

import models.game.gem.{ Color, Gem }

object GameTestFuse extends GameTest(6, 12) {
  override def init() = {
    board.add(Gem(0, Color.Red), 0, 0)
    board.add(Gem(1, Color.Red), 1, 0)
    board.add(Gem(2, Color.Red), 2, 0)
    board.add(Gem(3, Color.Red), 0, 1)
    board.add(Gem(4, Color.Red), 1, 1)
    board.add(Gem(5, Color.Red), 2, 1)
    board.add(Gem(6, Color.Red), 0, 2)
    board.add(Gem(7, Color.Red), 1, 2)
    board.add(Gem(8, Color.Red), 2, 2)
    board.add(Gem(9, Color.Green), 0, 3)
    board.add(Gem(10, Color.Blue, timer = Some(5)), 3, 0)

    testBoard.add(Gem(0, Color.Red, group = Some(1)), 0, 0)
    testBoard.add(Gem(1, Color.Red, group = Some(1)), 1, 0)
    testBoard.add(Gem(2, Color.Red, group = Some(1)), 2, 0)
    testBoard.add(Gem(3, Color.Red, group = Some(1)), 0, 1)
    testBoard.add(Gem(4, Color.Red, group = Some(1)), 1, 1)
    testBoard.add(Gem(5, Color.Red, group = Some(1)), 2, 1)
    testBoard.add(Gem(6, Color.Red, group = Some(1)), 0, 2)
    testBoard.add(Gem(7, Color.Red, group = Some(1)), 1, 2)
    testBoard.add(Gem(8, Color.Red, group = Some(1)), 2, 2)
    testBoard.add(Gem(9, Color.Green), 0, 3)
    testBoard.add(Gem(10, Color.Blue, timer = Some(5)), 3, 0)
  }

  override def run() = board.fuse()
}
