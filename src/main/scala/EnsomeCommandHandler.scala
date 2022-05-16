import zio.entity.core.Fold.impossible
import zio.{IO, UIO}
import zio.entity.core.{Combinators, Fold}
import zio.entity.data.{EntityProtocol, EventTag, Tagging}
import zio.entity.data.Tagging.Const
import zio.entity.macros.RpcMacro

class EnsomeCommandHandler(combinators: Combinators[EnsomeState, EnsomeEvent, String]) extends Ensome {
  import combinators._
  def feed(quantity: Int): IO[String, String] =
    read flatMap { res =>
      append(Ate(quantity)).as("잘먹음")
    }

  def workout(time: Int): IO[String, String] =
    read flatMap { res =>
      append(Workouted(time)).as("잘함")
    }

  def getWeight: IO[String, Int] = read map(_.weight)
}


object EnsomeCommandHandler{
  val tagging: Const[String] = Tagging.const[String](EventTag("Counter"))

  implicit val EnsomeProtocol: EntityProtocol[Ensome, String] =
    RpcMacro.derive[Ensome, String]

  val eventHandlerLogic: Fold[EnsomeState, EnsomeEvent] = Fold(
    initial = EnsomeState(120),
    reduce = {
      case (state, Ate(quantity)) => UIO.succeed(state.copy(state.weight + quantity))
      case (state, Workouted(time)) => UIO.succeed(state.copy(state.weight - (time * 2)))
      case _ => impossible
    }
  )
}