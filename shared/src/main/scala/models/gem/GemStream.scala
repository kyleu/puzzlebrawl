package models.gem

import models.Constants

import scala.util.Random

final case class GemStream(
    seed: Int = Math.abs(Random.nextInt),

    gemAdjustWild: Option[Double] = None,
    gemAdjustCrash: Option[Double] = None,

    gemAdjustRed: Option[Double] = None,
    gemAdjustGreen: Option[Double] = None,
    gemAdjustBlue: Option[Double] = None,
    gemAdjustYellow: Option[Double] = None,

    crashAdjustRed: Option[Double] = None,
    crashAdjustGreen: Option[Double] = None,
    crashAdjustBlue: Option[Double] = None,
    crashAdjustYellow: Option[Double] = None) {

  private[this] val r = new Random(seed)
  private[this] var nextId = 0
  private[this] var nextTimerId = 10000

  private[this] var pendingGems = Seq.empty[Gem]
  def addPendingGems(gems: Seq[Gem]) = pendingGems = pendingGems ++ gems

  private[this] val wildInterval = (Constants.GemStream.baseWildGemInterval * gemAdjustWild.getOrElse(1.0)).toInt
  private[this] val crashChance = Constants.GemStream.baseCrashGemChance * gemAdjustCrash.getOrElse(1.0)

  val baseChanceValue = 100.0

  private[this] val gemChances = Seq(
    Color.Red -> baseChanceValue * gemAdjustRed.getOrElse(1.0),
    Color.Green -> baseChanceValue * gemAdjustGreen.getOrElse(1.0),
    Color.Blue -> baseChanceValue * gemAdjustBlue.getOrElse(1.0),
    Color.Yellow -> baseChanceValue * gemAdjustYellow.getOrElse(1.0)
  )
  private[this] val gemChanceTotal = gemChances.map(_._2).sum

  private[this] val crashChances = Seq(
    Color.Red -> baseChanceValue * crashAdjustRed.getOrElse(1.0),
    Color.Green -> baseChanceValue * crashAdjustGreen.getOrElse(1.0),
    Color.Blue -> baseChanceValue * crashAdjustBlue.getOrElse(1.0),
    Color.Yellow -> baseChanceValue * crashAdjustYellow.getOrElse(1.0)
  )
  private[this] val crashChanceTotal = crashChances.map(_._2).sum

  def next() = {
    val ret = pendingGems.headOption match {
      case Some(g) =>
        pendingGems = pendingGems.tail
        g.copy(id = nextId)
      case None =>
        if (wildInterval > 0 && (nextId + 1) % wildInterval == 0) {
          Gem(nextId, color = Color.Wild)
        } else {
          val crash = if (r.nextDouble < crashChance) { Some(true) } else { None }
          val color = randomColor(crash.exists(x => x))
          Gem(nextId, color = color, crash = crash)
        }
    }
    nextId += 1
    ret
  }

  def nextTimer(color: Color) = {
    val ret = Gem(nextTimerId, color = color, timer = Some(5))
    nextTimerId += 1
    ret
  }

  private[this] def randomColor(crash: Boolean): Color = {
    val dist = if (crash) { crashChances } else { gemChances }
    val p = r.nextDouble * (if (crash) { crashChanceTotal } else { gemChanceTotal })
    var accum = 0.0
    dist.iterator.find { i =>
      val (item, itemProb) = i
      accum += itemProb
      accum >= p
    }.map(_._1).getOrElse(throw new IllegalStateException())
  }
}
