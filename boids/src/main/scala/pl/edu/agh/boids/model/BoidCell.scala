package pl.edu.agh.boids.model

import pl.edu.agh.xinuk.model.Cell.SmellArray
import pl.edu.agh.xinuk.model.{Cell, Signal, SmellingCell}

final case class BoidCell(smell: SmellArray, boidsCount: Int = 1) extends SmellingCell {
  override type Self = BoidCell
  override def withSmell(smell: SmellArray): BoidCell = copy(smell = smell)
}

object BoidCell {
  def create(initialSignal: Signal): BoidCell = BoidCell(Array.fill(Cell.Size, Cell.Size)(initialSignal))
}
