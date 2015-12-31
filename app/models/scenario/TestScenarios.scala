package models.scenario

import java.util.UUID

import models.Constants
import models.board.Board
import models.brawl.Brawl
import models.gem.{ Color, Gem, GemStream }
import models.player.Player
import models.test.gem.MockGemStreams
import models.user.PlayerRecord

object TestScenarios {
  private[this] val (w, h) = Constants.Board.defaultWidth -> Constants.Board.defaultHeight

  private[this] def withAis(players: Seq[PlayerRecord], totalPlayers: Int, script: String, seed: Int) = {
    val ps = players.zipWithIndex.map { p =>
      Player(p._1.userId, p._1.name, p._2, Board(p._1.name, Constants.Board.defaultWidth, Constants.Board.defaultHeight), GemStream(seed))
    }
    val ais = (0 until (totalPlayers - players.size)).map { i =>
      Player(UUID.randomUUID, "AI " + (i + 1), players.size + i, Board("AI " + (i + 1), w, h), GemStream(seed), script = Some(script))
    }
    ps ++ ais
  }

  private def inst(players: Seq[PlayerRecord], totalPlayers: Int, script: String, seed: Int, id: UUID, name: String) = {
    val all = withAis(players, totalPlayers, script, seed)
    all.foreach(_.activeGemsCreate())
    Brawl(id, name, seed, all)
  }

  def newInstance(id: UUID, scenario: String, seed: Int, players: Seq[PlayerRecord]) = scenario match {
    case "BasicAI" => inst(players, 5, "basic", seed, id, "Basic AI")
    case "OneOnOne" => inst(players, 2, "basic", seed, id, "1v1 AI")
    case "Speedy" => inst(players, 2, "speedy", seed, id, "Speedy AI")
    case "Spinner" => inst(players, 9, "spinner", seed, id, "Whee!")
    case "StressTest" => inst(players, 100, "random", seed, id, "Stress Test")
    case "TeamAI" =>
      val b = inst(players, 4, "random", seed, id, "Team AI")
      b.copy(players = b.players.zipWithIndex.map(x => x._1.copy(team = x._2 % 2)))
    case "Fixed" =>
      val ps = players.zipWithIndex.map { p =>
        val gs = new FixedGemStream(Seq(
          Gem(0), Gem(1),
          Gem(2, color = Color.Green), Gem(3, color = Color.Green, crash = Some(true)),
          Gem(4, color = Color.Blue), Gem(5, color = Color.Blue, crash = Some(true)),
          Gem(6, color = Color.Yellow), Gem(7, color = Color.Yellow, crash = Some(true)),
          Gem(8), Gem(9, crash = Some(true)),
          Gem(10), Gem(11)
        ))
        Player(p._1.userId, p._1.name, p._2, Board(p._1.name, Constants.Board.defaultWidth, Constants.Board.defaultHeight), gs)
      }
      ps.foreach(_.activeGemsCreate())
      Brawl(id, scenario, seed, ps)
    case x if x.startsWith("All") =>
      val testName = x.stripPrefix("All")
      val ps = players.zipWithIndex.map { p =>
        Player(p._1.userId, p._1.name, p._2, Board(p._1.name, w, h), MockGemStreams.forString(testName))
      }
      val brawl = Brawl(id, scenario, seed, ps)
      brawl.players.foreach(_.activeGemsCreate())
      brawl
    case x if x.startsWith("Test") => ScenarioBrawlHelper.testBrawl(id, scenario.stripPrefix("Test"), players)
    case x => throw new IllegalArgumentException(s"Invalid scenario [$scenario].")
  }
}
