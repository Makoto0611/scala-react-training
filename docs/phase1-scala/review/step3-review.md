# Step3 エラーハンドリング レビュー

**日時**: 2026年03月19日（木）18時06分 JST
**PR**: [#3 workspace/makoto-progress-step3](https://github.com/Makoto0611/scala-react-training/pull/3)

---

## 完成度

| Part | 内容 | 結果 |
|------|------|------|
| Part1-filter版 | `filter + headOption` で `Option[Content]` を返す | ✅ |
| Part1-find版 | `find` で `Option[Content]` を返す | ✅ |
| Part2 | `calcLikeRate` で `Either[String, Double]` を返す | ✅ |
| Part3 | `Try` でファイル読み込みをラップ | ✅ |
| Part4 | Q1〜Q3 の考察をコメントで記載 | △（追記余地あり） |

---

## よくできた点

- `Option` / `Either` / `Try` の三種類を全て自力で実装した
- `filter + headOption` と `find` の2バージョンを並べて比較しようとした探究心がある
- `Either` の `if / else if / else` 構造でビジネスルール違反（views=0、likes超過）を `Left` で表現できている
- `import scala.util.{Try, Success, Failure}` を自分で追加できた
- `Try(...)` でラップする形を正しく理解して実装できた

---

## 改善点・注意事項

### ① `match` が恒等変換になっている（重要度：高）

```scala
// 現在の実装
hasContent match {
  case Some(content) => hasContent  // Some をもらって Some を返す
  case None          => None        // None をもらって None を返す
}
```

`find` や `filter + headOption` がすでに `Option[Content]` を返している。  
その値に対してさらに `match` でラップし直す必要はなく、そのまま返せばよい。

```scala
// シンプルな正解
def findContent(contentId: String, contents: List[Content]): Option[Content] =
  contents.find(c => c.id == contentId)
```

また `case Some(content) => hasContent` では束縛した `content` を使わずに元の変数を返しており、意図しないコードになっている。

### ② `readJsonFile` の戻り値が `Unit`（重要度：高）

```scala
def readJsonFile(path: String): Unit = { ... }
```

`Unit` を返す関数は呼び出し元が結果を受け取って次の処理に繋げられない。  
「ファイルを読んで結果を返す」と「結果を画面に出力する」は分離すべき（SRP：単一責任の原則）。

```scala
// 正しい方向性
def readJsonFile(path: String): Try[String] = {
  Try(scala.io.Source.fromFile(path).mkString)
}
// 呼び出し元で match して println する
```

### ③ `Source` のクローズ漏れ（重要度：中）

```scala
Try(scala.io.Source.fromFile(path).mkString)
```

`Source.fromFile` はファイルハンドルを開くが、このコードでは読み込み後に `Source` をクローズしていない。  
`scala.util.Using` を使うとリソースを自動でクローズできる。

```
調べるキーワード: scala.util.Using、try-with-resources に相当するScalaの書き方
```

### ④ `calcLikeRate` の到達不能コード（重要度：低）

```scala
else {
  val rate = if (views == 0) 0.0 else likes.toDouble / views * 100
  //              ^^^^^^^^^ else ブロックに入っている時点で views != 0 が確定しているため不要
  Right(rate)
}
```

`else` ブロックに入っている時点で `views != 0` は確定しているため、内側の `if (views == 0)` は不要。

### ⑤ Part4 考察の追記ポイント

**Q1（`Either[String, Content]` のメリット・デメリット）**  
`Option` は「あるかないか」しか伝えられないが、`Either` は「なぜないか（エラー理由）」を伝えられる。  
デメリットは記述量が増え、Left/Right の使い分けを呼び出し元が意識する必要がある点。

**Q3（`Try` vs `try-catch`）**  
`try-catch` でも例外の種類で分岐は可能。本質的な違いは `Try` が**値**である点。  
値なので `.map` / `.flatMap` で合成でき、関数の戻り値として返せる。  
調べるキーワード：`Try#map`、`式指向（Expression-oriented）`、`参照透明性`

---

## Step4 への引き継ぎ

- `Option` / `Either` / `Try` は「エラーを値として扱う」という共通思想を持つ
- for-comprehension（for式 + yield）でこれらを組み合わせる書き方が Step4 以降で登場する
- `scala.util.Using` はリソース管理の標準的な書き方として覚えておく
