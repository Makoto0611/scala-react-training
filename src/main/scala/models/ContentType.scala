sealed trait ContentType

case object Movie extends ContentType
case class Series(seasons: Int) extends ContentType
case object Live extends ContentType
