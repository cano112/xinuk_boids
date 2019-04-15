package pl.edu.agh.boids.algorithm

import pl.edu.agh.boids.config.BoidsConfig
import pl.edu.agh.boids.model.BoidCell
import pl.edu.agh.boids.simulation.BoidsMetrics
import pl.edu.agh.xinuk.algorithm.MovesController
import pl.edu.agh.xinuk.model._

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
    Thread.sleep(config.delayMillis)
    val newGrid = Grid.empty(bufferZone)

    def copyCell(x: Int, y: Int, cell: GridPart): Unit = {
      newGrid.cells(x)(y) = cell
    }

    def moveCell(x: Int, y: Int, cell: GridPart): Unit = {
      val randomX = x + randomSignum * config.boidStepSize
      val randomY = y + randomSignum * config.boidStepSize
      val destination = (cropCoordOnBorder(randomX), cropCoordOnBorder(randomY))
      val vacatedCell = EmptyCell(cell.smell)
      val occupiedCell = BoidCell.create(Signal.Zero)
      newGrid.cells(destination._1)(destination._2) match {
        case EmptyCell(_) =>
          newGrid.cells(x)(y) = vacatedCell
          newGrid.cells(destination._1)(destination._2) = occupiedCell
        case BufferCell(EmptyCell(_)) =>
          newGrid.cells(x)(y) = vacatedCell
          newGrid.cells(destination._1)(destination._2) = BufferCell(occupiedCell)
        case _ =>
          newGrid.cells(x)(y) = occupiedCell
      }
    }

    val (dynamicCells, staticCells) = (for {
      x <- 0 until config.gridSize
      y <- 0 until config.gridSize
    } yield (x, y, grid.cells(x)(y))).partition({
      case (_, _, BoidCell(_)) => true
      case (_, _, _) => false
    })

    staticCells.foreach({ case (x, y, cell) => copyCell(x, y, cell) })
    dynamicCells.foreach({ case (x, y, cell) => moveCell(x, y, cell) })

    (newGrid, BoidsMetrics.empty())
  }

  private def randomSignum: Int = randomGen.nextInt(3) - 1

  private def cropCoordOnBorder(coord: Int): Int = {
    if(coord >= config.gridSize) return config.gridSize - 1
    if(coord < 0) return 0
    coord
  }
}
