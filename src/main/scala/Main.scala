object Main extends App {

  val content1 = Content("ct_001", "サンプル映画A", Movie, 5400)
  val content2 = Content("ct_002", "サンプルシリーズB", Series(3), 3600)
  val content3 = Content("ct_003", "サンプルライブC", Live, 0)

  val contents = List(content1, content2, content3)

  println("=== コンテンツ一覧 ===")
  contents.foreach { c =>
    val typeLabel = c.contentType match {
      case Movie     => "映画"
      case Series(n) => s"シリーズ(${n}シーズン)"
      case Live      => "ライブ"
    }
    println(s"${c.id} | ${c.title} | ${typeLabel} | 再生時間:${c.second}秒")
  }

  val view1 = View("ct_001", 0)
  val view2 = View("ct_001", 10)
  val view3 = View("ct_002", 20)
  val view4 = View("ct_003", 30)

  val views = List(view1, view2, view3, view4)

  println("=== 視聴ログ ===")
  views.foreach { v =>
    println(s"${v.id} | ${v.datetime + 1700000000}")
  }

  val like1 = Like("ct_001", 5)
  val like2 = Like("ct_002", 25)

  val likes = List(like1, like2)

  println("=== いいねログ ===")
  likes.foreach { l =>
    println(s"${l.id} | ${l.datetime+ 1700000000}")
  }
}
