package pl.edu.agh.boids.algorithm

import com.avsystem.commons
import com.avsystem.commons.SharedExtensions._
import com.avsystem.commons.misc.Opt
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
      grid.cells(randomX)(randomY) = BoidCell.create(Signal(1))
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

    def calculatePossibleDestinations(cell: GridPart, x: Int, y: Int, grid: Grid): Iterator[(Int, Int, GridPart)] = {
      val neighbourCellCoordinates = Grid.neighbourCellCoordinates(x, y)
      Grid.SubcellCoordinates
        .map { case (i, j) => cell.smell(i)(j) }
        .zipWithIndex
        .sorted(implicitly[Ordering[(Signal, Int)]].reverse)
        .iterator
        .map { case (_, idx) =>
          val (i, j) = neighbourCellCoordinates(idx)
          (i, j, grid.cells(i)(j))
        }
    }

    def selectDestinationCell(possibleDestinations: Iterator[(Int, Int, GridPart)], newGrid: Grid): commons.Opt[(Int, Int, GridPart)] = {
      possibleDestinations
        .map { case (i, j, current) => (i, j, current, newGrid.cells(i)(j)) }
        .collectFirstOpt {
          case (i, j, currentCell@EmptyCell(_), EmptyCell(_)) =>
            (i, j, currentCell)
          case (i, j, currentCell@BufferCell(EmptyCell(_)), BufferCell(EmptyCell(_))) =>
            (i, j, currentCell)
        }
    }

    def moveCell(x: Int, y: Int, cell: GridPart): Unit = {
      val destinations = calculatePossibleDestinations(cell, x, y, grid)
      val destination = selectDestinationCell(destinations, newGrid)
      val vacatedCell = EmptyCell(cell.smell)
      val occupiedCell = BoidCell.create(Signal(1))

      destination match {
        case Opt((i, j, EmptyCell(_))) =>
          newGrid.cells(x)(y) = vacatedCell
          newGrid.cells(i)(j) = occupiedCell
        case Opt((i, j, BufferCell(EmptyCell(_)))) =>
          newGrid.cells(x)(y) = vacatedCell
          newGrid.cells(i)(j) = BufferCell(occupiedCell)
        case Opt((i, j, notEmpty)) =>
          throw new RuntimeException(s"Boid selected not empty destination ($i,$j): $notEmpty")
        case Opt.Empty =>
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
