package models.game.board

import models.game.board.mutation.Mutation
import models.game.board.mutation.Mutation.RemoveGem
import models.game.gem.Gem

trait CrashHelper { this: Board =>
  def crash(): Seq[Seq[Mutation]] = mapGems { (gem, x, y) =>
    if (gem.crash) {
      crashGem(gem, x, y)
    } else {
      Seq.empty
    }
  }

  private[this] def crashGem(gem: Gem, x: Int, y: Int) = {
    if (!gem.crash) {
      throw new IllegalStateException(s"Crash called at [$x, $y], which is occupied by non-crash gem $gem.")
    }

    val encountered = scala.collection.mutable.HashSet.empty[Int]

    def check(source: Gem, gem: Gem, x: Int, y: Int): Seq[(Gem, Int, Int)] = {
      if (gem.timer.isEmpty && gem.color == source.color && !encountered.contains(gem.id)) {
        encountered += gem.id
        (gem, x, y) +: Seq(
          at(x, y + 1).map(g => check(source, g, x, y + 1)), // u
          at(x + 1, y).map(g => check(source, g, x + 1, y)), // r
          at(x, y - 1).map(g => check(source, g, x, y - 1)), // d
          at(x - 1, y).map(g => check(source, g, x - 1, y)) // l
        ).flatten.flatten
      } else {
        Seq.empty
      }
    }

    val run = check(gem, gem, x, y)
    if (run.size > 1) {
      run.map(n => applyMutation(RemoveGem(n._2, n._3)))
    } else {
      Seq.empty
    }
  }
}
