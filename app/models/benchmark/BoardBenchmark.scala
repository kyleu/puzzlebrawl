package models.benchmark

import java.util.concurrent.TimeUnit

import models.game.Game
import org.openjdk.jmh.annotations._

import scala.util.Random

class BoardBenchmark {
  @Benchmark
  @Measurement(iterations = 4, time = 5, timeUnit = TimeUnit.SECONDS)
  @Warmup(iterations = 2, time = 5, timeUnit = TimeUnit.SECONDS)
  @Threads(6)
  @Fork(1)
  def creation() = {
    val game = Game.blank()
    val board = game.players.head.board
    val gemStream = game.players.head.gemStream

    for(i <- 0 until 20) {
      board.drop(gemStream.next, Random.nextInt(6))
    }

    board.fuse()
    board.crash()
    board.collapse()
  }
}
