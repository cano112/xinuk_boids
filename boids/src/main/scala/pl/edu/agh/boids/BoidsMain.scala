package pl.edu.agh.boids

import java.awt.Color

import com.typesafe.scalalogging.LazyLogging
import pl.edu.agh.boids.algorithm.BoidsMovesController
import pl.edu.agh.boids.model.BoidCell
import pl.edu.agh.boids.model.parallel.BoidPositionConflictResolver
import pl.edu.agh.xinuk.Simulation
import pl.edu.agh.xinuk.model.{DefaultSmellPropagation, Obstacle}

object BoidsMain extends LazyLogging {
  private val configPrefix = "boids"
  private val metricHeaders = Vector()

  def main(args: Array[String]): Unit = {
    import pl.edu.agh.xinuk.config.ValueReaders._
    new Simulation(
      configPrefix,
      metricHeaders,
      BoidPositionConflictResolver,
      DefaultSmellPropagation.calculateSmellAddendsStandard)(new BoidsMovesController(_)(_),
      {
        case _: BoidCell => Color.WHITE
        case Obstacle => Color.BLUE
      }).start()
  }
}

