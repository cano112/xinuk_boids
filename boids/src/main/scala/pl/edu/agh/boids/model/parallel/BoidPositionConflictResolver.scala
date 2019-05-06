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
      case (EmptyCell(currentSmell), BoidCell(incomingSmell)) =>
        (BoidCell(currentSmell + incomingSmell), BoidsMetrics.empty())
      case (EmptyCell(currentSmell), EmptyCell(incomingSmell)) =>
        (EmptyCell(currentSmell + incomingSmell), BoidsMetrics.empty())
      case (BoidCell(currentSmell), EmptyCell(incomingSmell)) =>
        (BoidCell(currentSmell + incomingSmell), BoidsMetrics.empty())
      case (BoidCell(currentSmell), BoidCell(incomingSmell)) =>
        (BoidCell(currentSmell + incomingSmell), BoidsMetrics.empty())
      case (x, y) => throw new UnsupportedOperationException(s"Unresolved conflict: $x with $y")
    }
  }
}
