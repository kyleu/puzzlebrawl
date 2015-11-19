package models.game.test

import models.game.gem.{ GemStream, Color, Gem }

import scala.util.Random

object GameTestGemStream extends GameTest.Provider {
  override def newInstance() = GameTestGemStream()
}

case class GameTestGemStream() extends GameTest(seed = 0) {
  override def init() = {
    for(y <- 0 until 12) {
      for(x <- 0 until 6) {
        goal.add(Gem((y * 6) + x, Color.Red), x, y)
      }
    }
  }

  override def run() = {
    val seed = Math.abs(Random.nextInt)
    val stream = GemStream(seed = seed, gemAdjustWild = 0, gemAdjustCrash = 0, gemAdjustRed = 1.0, gemAdjustGreen = 0, gemAdjustBlue = 0, gemAdjustYellow = 0)
    for(y <- 0 until 12) {
      for(x <- 0 until 6) {
        board.add(stream.next, x, y)
      }
    }
  }
}