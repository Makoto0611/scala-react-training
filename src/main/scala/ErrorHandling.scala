import scala.util.{Try, Success, Failure}

//Part1
// 関数名：findContent
// 引数  ：contentId: String, contents: List[Content]
// 戻り値：Option[Content]
// 仕様  ：contents の中に contentId と一致する Content があれば Some(content) を返す。なければ None を返す。
def filterContent(contentId: String, contents: List[Content]): Option[Content] = {
  contents.filter(c => c.id == contentId).headOption
}

def findContent(contentId: String, contents: List[Content]): Option[Content] = {
  contents.find(c => c.id == contentId)
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
    val rate = likes.toDouble / views * 100
    Right(rate)
  }
}

// Part3
def readJsonFile(path: String): Try[String] = {
  Try(scala.io.Source.fromFile(path).mkString)
}

// Part4
// Q1: findContent の戻り値を Either[String, Content] にするメリット・デメリットは何か
// 呼び出し元のエラーで何が原因になっているのかを検知できる。半面rightとleftを両方処理しなければいけない

// Q2: calcLikeRate の戻り値を Option[Double] にした場合と Either[String, Double] にした場合、
//  呼び出し元でどんな違いが出るか
//　エラー時にleftのStringを使うことで、詳細なエラーメッセージを出力でき原因がわかりやすくなる。

// Q3: Try を使わずに try-catch で書いた場合と比べて何が変わるか
// Tryは成功か失敗を値として持つので、その値を関数の戻り値をしても利用することができる
