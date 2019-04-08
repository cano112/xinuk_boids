package pl.edu.agh.boids.simulation

import pl.edu.agh.xinuk.simulation.Metrics

final case class BoidsMetrics() extends Metrics {
  override def log: String = {
    s""
  }

  override def series: Vector[(String, Double)] = Vector()

  override def +(other: Metrics): BoidsMetrics = {
    this
  }
}

object BoidsMetrics {
  private val EMPTY = BoidsMetrics()

  def empty(): BoidsMetrics = EMPTY
}
