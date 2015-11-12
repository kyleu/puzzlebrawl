package models.game.board

import models.game.board.Board.RemoveGem
import models.game.gem.Gem

trait CrashHelper { this: Board =>
  def crash() = {
    val encountered = scala.collection.mutable.HashSet.empty[Int]

    def check(source: Gem, gem: Gem, x: Int, y: Int): Seq[(Gem, Int, Int)] = {
      if(gem.timer.isEmpty && gem.color == source.color && !encountered.contains(gem.id)) {
        encountered += gem.id
        (gem, x, y) +: Seq(
          at(x, y + 1).map(g => check(source, g, x, y + 1)), // u
          at(x + 1, y).map(g => check(source, g, x + 1, y )), // r
          at(x, y - 1).map(g => check(source, g, x, y - 1)), // d
          at(x - 1, y).map(g => check(source, g, x - 1, y)) // l
        ).flatten.flatten
      } else {
        Seq.empty
      }
    }

    mapGems { (gem, x, y) =>
      if(gem.crash) {
        val run = check(gem, gem, x, y)
        run.map { n =>
          val msg = RemoveGem(n._2, n._3)
          applyMutation(msg)
          msg
        }
      } else {
        Seq.empty
      }
    }.flatten
  }
}