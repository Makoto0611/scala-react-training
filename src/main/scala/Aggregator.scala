import scala.collection.MapView
object Aggregator extends App {
  val view1 = View("ct_001", 0)
  val view2 = View("ct_001", 10)
  val view3 = View("ct_002", 20)
  val view4 = View("ct_003", 30)
  val views = List(view1, view2, view3, view4)

  val like1 = Like("ct_001", 5)
  val like2 = Like("ct_002", 25)
  val likes = List(like1, like2)

  val content1 = Content("ct_001", "サンプル映画A", Movie, 5400)
  val content2 = Content("ct_002", "サンプルシリーズB", Series(3), 3600)
  val content3 = Content("ct_003", "サンプルライブC", Live, 0)
  val contents = List(content1, content2, content3)

  // part1
  // 1. views から contentId が "ct_001" のものだけ抽出する
  val ct_001_only = views.filter { v => v.id == "ct_001" }
  println(s"${ct_001_only}")

  // 2. views の全 contentId を List にする（重複あり）
  val viewIds = views.map { view => view.id }
  println(s"${viewIds}")

  // 3. contents に存在する contentId の視聴ログのみ抽出する（ct_003はcontentsにないと仮定）
  val contentIds: List[String] = contents.map(c => c.id)
  val filtered: List[View] = views.filter(v => contentIds.contains(v.id))
  println(s"${filtered}")

  // 4. 再生時間が 1800秒以上のコンテンツタイトルだけの List を作る
  val filtered2: List[Content] = contents.filter(c => c.second >= 1800)
  val contTitle: List[String] = filtered2.map(f => f.title)
  println(s"${contTitle}")

  // 5. 再生時間の合計を foldLeft で計算する（size・sum は使わない）
  val amount = contents.foldLeft(0)((acc, c) => acc + c.second)
  println(s"${amount}")

  // part2
  //   1. views を contentId ごとにグループ化する
  //    → 型は Map[String, List[View]] になる
  val viewGroup: Map[String, List[View]] = views.groupBy(v => v.id)
  println(s"${viewGroup}")

  // 2. likes を contentId ごとにグループ化する
  //    → 型は Map[String, List[Like]] になる
  val likesGroup: Map[String, List[Like]] = likes.groupBy(l => l.id)
  println(s"${likesGroup}")

  // 3. 各 contentId の視聴件数を Map にする
  //    → 期待値：Map("ct_001" -> 2, "ct_002" -> 1, "ct_003" -> 1)
  val viewCount: Map[String, Int] = viewGroup.mapValues(_.size).toMap()
  println(s"${viewCount}")

  // 4. 各 contentId のいいね件数を Map にする
  //    → ct_003 はいいねデータが存在しないため 0 になる（getOrElse を使う）
  val likeCount: Map[String, Int] = likesGroup.mapValues(_.size).toMap()

  // part3
  println("=== 集計結果 ===")
  contents.foreach(c => {
    val vc = viewCount.getOrElse(c.id, 0)
    val lc = likeCount.getOrElse(c.id, 0)
    val rate = if (vc == 0) 0.0 else lc.toDouble / vc * 100
    println(f"${c.id}: views=${vc}, likes=${lc}, likeRate=${rate}%.2f%%")
  })

  //  part4
  //  課題内容:Part3 の集計処理を for式（for comprehension）で書き直したバージョンを作り、 同じ出力が出ることを確認する。
  //  docs/phase1-scala/step2-collections/assignment.md
  for (c <- contents) {
    val vc = viewCount.getOrElse(c.id, 0)
    val lc = likeCount.getOrElse(c.id, 0)
    val rate = if (vc == 0) 0.0 else lc.toDouble / vc * 100
    println(f"${c.id}: views=${vc}, likes=${lc}, likeRate=${rate}%.2f%%")
  }
}
