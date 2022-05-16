import zio.IO
import zio.entity.annotations.Id

trait Ensome {
  @Id(1)
  def feed(quantity: Int): IO[String, String]

  @Id(2)
  def workout(time: Int): IO[String, String]

  @Id(3)
  def getWeight: IO[String, Int]
}
