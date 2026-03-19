import scala.util.{Try, Success, Failure}

//Part1
// 関数名：findContent
// 引数  ：contentId: String, contents: List[Content]
// 戻り値：Option[Content]
// 仕様  ：contents の中に contentId と一致する Content があれば Some(content) を返す。なければ None を返す。
def findContent(contentId: String, contents: List[Content]): Unit = {

  val contentOpt = contents.filter(c => c.id == contentId).headOption
  contentOpt match {
    case Some(content) => println(s"${content.id} が見つかりました: ${content.title}")
    case None          => println(s"${contentId} は見つかりませんでした")
  }

  val hasContent = contents.find(c => c.id == contentId)
  hasContent match {
    case Some(content) => println(s"${content.id} が見つかりました: ${content.title}")
    case None          => println(s"${contentId} は見つかりませんでした")
  }

  // Part2
  // 関数名：calcLikeRate
  // 引数  ：views: Int, likes: Int
  // 戻り値：Either[String, Double]
  // 仕様  ：
  //   - views が 0 の場合は Left("視聴数が0のためいいね率を計算できません")
  //   - likes が views より大きい場合は Left("いいね数が視聴数を超えています")
  //   - 正常な場合は Right(いいね率) を返す（0.0〜100.0の値）
  def calcLikeRate(views: Int, likes: Int): Either[String, Double] = {
    if (views == 0) { Left(s"視聴数が0のためいいね率を計算できません") }
    else if (views < likes) { Left("いいね数が視聴数を超えています") }
    else {
      val rate = if (views == 0) 0.0 else likes.toDouble / views * 100
      Right(rate)
    }
  }

  // Part3
  def readJsonFile(path: String): Unit = {
    val hasPath = Try(scala.io.Source.fromFile(path).mkString)
    hasPath match {
      case Success(i)            => println(s"success ${i}")
      case Failure(e: Throwable) =>
        println(s"ファイルの読み込みに失敗しました: + ${e.getMessage()}")
    }
  }

  // Part4
  // Q1: findContent の戻り値を Either[String, Content] にするメリット・デメリットは何か
  // Unitにしてしまっているのですが、メリットは厳密な型定義により堅牢になり、デメリットは逆に戻り値を柔軟にしたい場合に不便

  // Q2: calcLikeRate の戻り値を Option[Double] にした場合と Either[String, Double] にした場合、
  //     呼び出し元でどんな違いが出るか
  //　エラーのメッセージを文章にして出せない、異常値が出てもDouble方でしか返せない。

  // Q3: Try を使わずに try-catch で書いた場合と比べて何が変わるか
  // Tryのほうは条件によってエラー出力の分岐が作れる。try-catchの場合はエラーの分類によって分岐が作れる。
}
