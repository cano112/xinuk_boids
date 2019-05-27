package pl.edu.agh.boids.model.parallel

import pl.edu.agh.boids.config.BoidsConfig
import pl.edu.agh.boids.model.BoidCell
import pl.edu.agh.boids.simulation.BoidsMetrics
import pl.edu.agh.xinuk.model.{EmptyCell, GridPart, Obstacle, SmellingCell}
import pl.edu.agh.xinuk.model.parallel.ConflictResolver

object BoidPositionConflictResolver extends ConflictResolver[BoidsConfig] {

  import pl.edu.agh.xinuk.model.Cell._

  override def resolveConflict(current: GridPart, incoming: SmellingCell)(implicit config: BoidsConfig): (GridPart, BoidsMetrics) = {
    (current, incoming) match {
      case (Obstacle, _) =>
        (Obstacle, BoidsMetrics.empty())
      case (EmptyCell(currentSmell), BoidCell(incomingSmell, incomingBoids)) =>
        (BoidCell(currentSmell + incomingSmell, incomingBoids), BoidsMetrics.empty())
      case (EmptyCell(currentSmell), EmptyCell(incomingSmell)) =>
        (EmptyCell(currentSmell + incomingSmell), BoidsMetrics.empty())
      case (BoidCell(currentSmell, currentBoids), EmptyCell(incomingSmell)) =>
        (BoidCell(currentSmell + incomingSmell, currentBoids), BoidsMetrics.empty())
      case (current: BoidCell, incoming: BoidCell) =>
        val boid = BoidCell(current.smell + incoming.smell, current.boidsCount + incoming.boidsCount)
        (boid, BoidsMetrics.empty())
      case (x, y) => throw new UnsupportedOperationException(s"Unresolved conflict: $x with $y")
    }
  }
}
