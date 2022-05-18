import zio.entity.core.Fold.impossible
import zio.{Task, UIO}


final case class EnsomeState(
    weight: Int
){
  def handleEvent(e: EnsomeEvent): Task[EnsomeState] = e match {
    case Ate(quantity) => UIO.succeed(copy(weight + quantity))
    case Workouted(time) => UIO.succeed(copy(weight - (time * 2)))
    case _ => impossible
  }
}
