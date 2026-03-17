# Step1 復習メモ：データモデル設計と実装

**対象**: `sealed trait` / `case class` / `case object` / パターンマッチ / `foreach`

---

## 1. sealed trait と case object / case class の使い分け

```scala
sealed trait ContentType         // 種別の「親」を定義

case object Movie extends ContentType   // フィールドなし → case object
case class Series(seasons: Int) extends ContentType  // フィールドあり → case class
case object Live extends ContentType    // フィールドなし → case object
```

**ポイント**
- `sealed` にするとコンパイラがパターンマッチの網羅性をチェックしてくれる
- フィールドがない → `case object`（インスタンスが1つだけ）
- フィールドがある → `case class`（毎回新しいインスタンスを作る）
- `case object Movie()` のように `()` をつけるのは間違い

---

## 2. case class の定義

```scala
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
```

**ポイント**
- `type` はScalaの予約語なので使えない → `contentType` など別名を使う
- IDは計算に使わないので `String` 型が自然（`ct_001` のような値）
- フィールドの順番は呼び出し時と一致させる
- デフォルト引数（`= ...`）は省略可能にしたいフィールドに使う

---

## 3. インスタンスの作り方

```scala
val content1 = Content("ct_001", "サンプル映画A", Movie, 5400)
val content2 = Content("ct_002", "サンプルシリーズB", Series(3), 3600)
val content3 = Content("ct_003", "サンプルライブC", Live, 0)

// Long型：文字列ではなく数値として扱う → 演算（加算）ができる
val view1 = View("ct_001", 0)
val view2 = View("ct_001", 10)
val view3 = View("ct_002", 20)
val view4 = View("ct_003", 30)

val like1 = Like("ct_001", 5)
val like2 = Like("ct_002", 25)
```

**ポイント**
- `Long` 型は整数の演算ができる（`String` 型と違い `+` で連結にならない）
- ベース値からの加算で各インスタンスの時刻を表現できる

---

## 4. foreach と match の組み合わせ

```scala
val contents = List(content1, content2, content3)

contents.foreach { c =>
    val typeLabel = c.contentType match {
        case Movie     => "映画"
        case Series(n) => s"シリーズ(${n}シーズン)"
        case Live      => "ライブ"
    }
    println(s"${c.id} | ${c.title} | ${typeLabel} | 再生時間:${c.second}秒")
}
```

**ポイント**
- `foreach` はリストの各要素に処理を適用する
- `match` でパターンマッチする（`if-else` の代わり）
- `Series(n)` のように case class のフィールドを `n` として取り出せる
- `s"..."` は文字列補間（`${}` で変数を埋め込む）

---

## 5. View / Like の出力

```scala
val views = List(view1, view2, view3, view4)

println("=== 視聴ログ ===")
views.foreach { v =>
    println(s"${v.id} | ${v.datetime + 1700000000}")
}

val likes = List(like1, like2)

println("=== いいねログ ===")
likes.foreach { l =>
    println(s"${l.id} | ${l.datetime + 1700000000}")
}
```

**ポイント**
- `v.datetime + 1700000000` → `Long` 型なので加算で実際の時刻を計算できる
- 出力パターンは `Content` の `foreach` と同じ構造

---

## 6. sbt の構成

```
src/main/scala/        ← sbt が参照するソースディレクトリ
  models/
    ContentType.scala
    ContentModels.scala
  Main.scala
```

- `object Main extends App { ... }` がエントリーポイント
- `sbt run` で実行

---

## 確認問題

1. フィールドを持たない種別に `case class` を使うとどんな問題がある？
2. `sealed` をつける理由は？
3. `case Series(n)` の `n` は何を表している？
4. `s"${c.id}"` の `${}` は何のための記法？
5. `View` の `datetime` を `String` 型にした場合、`+ 1700000000` はどうなる？
