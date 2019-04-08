package pl.edu.agh.boids.algorithm

import pl.edu.agh.boids.config.BoidsConfig
import pl.edu.agh.boids.model.BoidCell
import pl.edu.agh.boids.simulation.BoidsMetrics
import pl.edu.agh.xinuk.algorithm.MovesController
import pl.edu.agh.xinuk.model.{Grid, Signal}

import scala.collection.immutable.TreeSet

final class BoidsMovesController(bufferZone: TreeSet[(Int, Int)])(implicit config: BoidsConfig) extends MovesController {

  override def initialGrid: (Grid, BoidsMetrics) = {
    val grid = Grid.empty(bufferZone)

    grid.cells(config.gridSize / 4)(config.gridSize / 4) = BoidCell.create(Signal.Zero)

    val metrics = BoidsMetrics.empty()
    (grid, metrics)
  }

  override def makeMoves(iteration: Long, grid: Grid): (Grid, BoidsMetrics) = {
    (grid, BoidsMetrics.empty())
  }
}
