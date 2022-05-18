import zio._
import zio.clock.Clock
import zio.console.putStrLn
import zio.duration.durationInt
import zio.entity.core.Entity.entity
import zio.entity.core.{Entity, EventSourcedBehaviour, MemoryStores, Stores}
import zio.entity.runtime.akka.Runtime
import EnsomeCommandHandler.EnsomeProtocol

object Main extends App {
  private val stores: ZLayer[Any, Nothing, Has[Stores[String, EnsomeEvent, EnsomeState]]] =
    Clock.live to MemoryStores.make[String, EnsomeEvent, EnsomeState](100.millis, 2)

  private val ensome: ZLayer[ZEnv, Throwable, Has[Entity[String, Ensome, EnsomeState, EnsomeEvent, String]]] =
    (Clock.live and stores and Runtime.actorSettings("Test")) to Runtime
      .entityLive(
        "Ensome",
        EnsomeCommandHandler.tagging,
        EventSourcedBehaviour[Ensome, EnsomeState, EnsomeEvent, String](
          new EnsomeCommandHandler(_),
          EnsomeCommandHandler.eventHandlerLogic,
          _.getMessage
        )
      )
      .toLayer

  val program = (for {
    ensome <- entity[String, Ensome, EnsomeState, EnsomeEvent, String]
    _      <- ensome("key").feed(300)
    _      <- ensome("key").workout(120)
    weight <- ensome("key").getWeight
    _      <- putStrLn(s"weight: $weight")
  } yield ()).provideCustomLayer(ensome)

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    program.exitCode

}
