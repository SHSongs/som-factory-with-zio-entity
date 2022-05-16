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

  private val counter: ZLayer[ZEnv, Throwable, Has[Entity[String, Ensome, EnsomeState, EnsomeEvent, String]]] =
    (Clock.live and stores and Runtime.actorSettings("Test")) to Runtime
      .entityLive("Counter", EnsomeCommandHandler.tagging,
        EventSourcedBehaviour[Ensome, EnsomeState, EnsomeEvent, String](
          new EnsomeCommandHandler(_), EnsomeCommandHandler.eventHandlerLogic, _.getMessage))
      .toLayer

  val program = (for {
    counter <- entity[String, Ensome, EnsomeState, EnsomeEvent, String]
    _ <- counter("key").feed(300)
    _ <- counter("key").workout(120)
    weight <- counter("key").getWeight
    _ <- putStrLn(s"weight: ${weight}")
  } yield ()).provideCustomLayer(counter)

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
   program.exitCode

}