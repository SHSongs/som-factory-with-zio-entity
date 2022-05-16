sealed trait EnsomeEvent

case class Ate(quantity: Int) extends EnsomeEvent

case class Workouted(time: Int) extends EnsomeEvent
