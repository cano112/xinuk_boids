package pl.edu.agh.boids.algorithm

import pl.edu.agh.boids.config.BoidsConfig
import pl.edu.agh.boids.model.BoidCell
import pl.edu.agh.boids.simulation.BoidsMetrics
import pl.edu.agh.xinuk.algorithm.MovesController
import pl.edu.agh.xinuk.model.{Grid, Signal}

import scala.collection.immutable.TreeSet
import scala.util.Random

final class BoidsMovesController(bufferZone: TreeSet[(Int, Int)])(implicit config: BoidsConfig) extends MovesController {

  private val randomGen = new Random()

  override def initialGrid: (Grid, BoidsMetrics) = {
    val grid = Grid.empty(bufferZone)
    for(i <- 0 until config.boidsCount) {
      val randomX = randomizeCoord()
      val randomY = randomizeCoord()
      grid.cells(randomX)(randomY) = BoidCell.create(Signal.Zero)
    }

    val metrics = BoidsMetrics.empty()
    (grid, metrics)
  }


  private def randomizeCoord() = randomGen.nextInt(config.gridSize - 2) + 1

  override def makeMoves(iteration: Long, grid: Grid): (Grid, BoidsMetrics) = {
    (grid, BoidsMetrics.empty())
  }
}
