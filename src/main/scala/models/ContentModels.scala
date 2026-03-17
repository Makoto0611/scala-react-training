case class Content(
    id: String,
    title: String,
    contentType: ContentType,
    second: Int,
)

case class View(
    id: String,
    datetime: Long = System.currentTimeMillis() / 1000
)

case class Like(
    id: String,
    datetime: Long = System.currentTimeMillis() / 1000
)
