package models.gem

object StaticGemPatterns {
  lazy val horizontal = GemPattern("horizontal", Seq(
    Seq(Color.Yellow, Color.Yellow, Color.Yellow, Color.Yellow, Color.Yellow, Color.Yellow),
    Seq(Color.Blue, Color.Blue, Color.Blue, Color.Blue, Color.Blue, Color.Blue),
    Seq(Color.Green, Color.Green, Color.Green, Color.Green, Color.Green, Color.Green),
    Seq(Color.Red, Color.Red, Color.Red, Color.Red, Color.Red, Color.Red)
  ).reverse)

  lazy val vertical = GemPattern("vertical", Seq(
    Seq(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Red, Color.Green),
    Seq(Color.Blue, Color.Yellow, Color.Red, Color.Green, Color.Blue, Color.Yellow),
    Seq(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Red, Color.Green),
    Seq(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Red, Color.Green)
  ).reverse)

  lazy val boxes = GemPattern("boxes", Seq(
    Seq(Color.Yellow, Color.Yellow, Color.Red, Color.Red, Color.Green, Color.Green),
    Seq(Color.Yellow, Color.Yellow, Color.Red, Color.Red, Color.Green, Color.Green),
    Seq(Color.Red, Color.Red, Color.Green, Color.Green, Color.Blue, Color.Blue),
    Seq(Color.Red, Color.Red, Color.Green, Color.Green, Color.Blue, Color.Blue)
  ).reverse)

  lazy val foolish = GemPattern("foolish", Seq(
    Seq(Color.Red, Color.Red, Color.Red, Color.Red, Color.Red, Color.Red),
    Seq(Color.Red, Color.Red, Color.Red, Color.Red, Color.Red, Color.Red),
    Seq(Color.Red, Color.Red, Color.Red, Color.Red, Color.Red, Color.Red),
    Seq(Color.Red, Color.Red, Color.Red, Color.Red, Color.Red, Color.Red)
  ).reverse)

  lazy val grid = GemPattern("grid", Seq(
    Seq(Color.Yellow, Color.Blue, Color.Blue, Color.Blue, Color.Blue, Color.Green),
    Seq(Color.Yellow, Color.Red, Color.Red, Color.Red, Color.Red, Color.Green),
    Seq(Color.Green, Color.Blue, Color.Blue, Color.Blue, Color.Blue, Color.Yellow),
    Seq(Color.Green, Color.Red, Color.Red, Color.Red, Color.Red, Color.Yellow)
  ).reverse)

  lazy val invaders = GemPattern("invaders", Seq(
    Seq(Color.Yellow, Color.Blue, Color.Green, Color.Green, Color.Blue, Color.Yellow),
    Seq(Color.Yellow, Color.Blue, Color.Green, Color.Red, Color.Blue, Color.Yellow),
    Seq(Color.Blue, Color.Yellow, Color.Green, Color.Red, Color.Yellow, Color.Blue),
    Seq(Color.Blue, Color.Yellow, Color.Red, Color.Red, Color.Yellow, Color.Blue)
  ).reverse)

  lazy val rainfall = GemPattern("rainfall", Seq(
    Seq(Color.Blue, Color.Red, Color.Yellow, Color.Blue, Color.Green, Color.Green),
    Seq(Color.Blue, Color.Green, Color.Red, Color.Yellow, Color.Blue, Color.Green),
    Seq(Color.Blue, Color.Blue, Color.Green, Color.Red, Color.Yellow, Color.Green),
    Seq(Color.Blue, Color.Yellow, Color.Blue, Color.Green, Color.Red, Color.Green)
  ).reverse)

  lazy val rampart = GemPattern("rampart", Seq(
    Seq(Color.Green, Color.Green, Color.Green, Color.Blue, Color.Blue, Color.Blue),
    Seq(Color.Green, Color.Green, Color.Green, Color.Blue, Color.Blue, Color.Blue),
    Seq(Color.Red, Color.Yellow, Color.Red, Color.Yellow, Color.Red, Color.Yellow),
    Seq(Color.Red, Color.Yellow, Color.Red, Color.Yellow, Color.Red, Color.Yellow)
  ).reverse)

  lazy val tetrino = GemPattern("tetrino", Seq(
    Seq(Color.Yellow, Color.Blue, Color.Blue, Color.Red, Color.Red, Color.Green),
    Seq(Color.Green, Color.Red, Color.Blue, Color.Red, Color.Blue, Color.Yellow),
    Seq(Color.Yellow, Color.Red, Color.Blue, Color.Red, Color.Blue, Color.Green),
    Seq(Color.Green, Color.Red, Color.Red, Color.Blue, Color.Blue, Color.Yellow)
  ).reverse)

  lazy val slip = GemPattern("slip", Seq(
    Seq(Color.Red, Color.Yellow, Color.Blue, Color.Green, Color.Red, Color.Yellow),
    Seq(Color.Yellow, Color.Blue, Color.Green, Color.Red, Color.Yellow, Color.Blue),
    Seq(Color.Blue, Color.Green, Color.Red, Color.Yellow, Color.Blue, Color.Green),
    Seq(Color.Green, Color.Red, Color.Yellow, Color.Blue, Color.Green, Color.Red)
  ).reverse)

  lazy val slide = GemPattern("slide", Seq(
    Seq(Color.Yellow, Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Red),
    Seq(Color.Blue, Color.Yellow, Color.Red, Color.Green, Color.Blue, Color.Yellow),
    Seq(Color.Green, Color.Blue, Color.Yellow, Color.Red, Color.Green, Color.Blue),
    Seq(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Red, Color.Green)
  ).reverse)
}

