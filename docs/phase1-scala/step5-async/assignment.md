# Step5 - 非同期処理

**参照書籍**: 実践Scala入門 第5章
**目安時間**: 3〜5時間

---

## 読む箇所

実践Scala入門 第5章を全て読む。

- 並行プログラミングのメリットとデメリット
- Futureの基本的な使い方
- Futureを扱うためのAPI

---

## 課題

課題3（レコメンドAPI）では複数のコンテンツを並行して処理する。
その準備として、Future を使った非同期処理を実装する。

### Part1：Future の基本

以下の処理を Future で実装する。

```scala
// 重い処理（スリープで疑似的に表現）
def fetchContent(contentId: String): Future[Content] = ???
def fetchViewCount(contentId: String): Future[Int] = ???
```

2つの Future を並行して実行し、両方が完了したら結果を出力する。
逐次実行（for式）と並行実行（zip / Future.sequence）の両方を実装し、
どちらが速いか確認すること。

### Part2：エラーハンドリングとの組み合わせ

```scala
def fetchContentWithError(contentId: String): Future[Either[String, Content]] = ???
```

Future が失敗した場合（例外）と、Either が Left の場合（ビジネスエラー）を
それぞれ異なる方法でハンドリングする実装を書く。

### Part3：複数の contentId を並行処理する

以下の仕様で実装する。

```
関数名：fetchAllContents
引数  ：contentIds: List[String]
戻り値：Future[List[Content]]
仕様  ：全ての contentId に対して fetchContent を並行で実行し、全部完了したら List にまとめて返す
```

`Future.sequence` を使うこと。

---

## 完了条件

- [ ] Part1 の並行実行が動作し、逐次より速いことが確認できる
- [ ] Part2 のエラーハンドリングが Future 失敗と Either Left の両方に対応している
- [ ] Part3 の fetchAllContents が動作する
- [ ] `Await.result` は動作確認用にのみ使い、本番コードでは使わない

---

## 詰まったときの調べ方

| 詰まりポイント | 調べる場所 |
|-------------|-----------|
| Future の基本 | 実践Scala入門 第5章 |
| ExecutionContext | `Scala Future ExecutionContext` |
| for式で Future を繋げる | 実践Scala入門 第5章 |
| Future.sequence | `Scala Future.sequence` |
| zip の使い方 | 実践Scala入門 第5章 |
| recover / recoverWith | `Scala Future recover` |

<details>
<summary>ヒント1：Future の基本</summary>

```scala
import scala.concurrent.{Future, Await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

val f: Future[Int] = Future {
  Thread.sleep(100)
  42
}
```

</details>

<details>
<summary>ヒント2：並行実行と逐次実行の違い</summary>

逐次（遅い）:
```scala
for {
  a <- fetchContent("ct_001")    // 完了してから
  b <- fetchContent("ct_002")    // 次を実行
} yield (a, b)
```

並行（速い）:
```scala
val fa = fetchContent("ct_001")  // 同時に
val fb = fetchContent("ct_002")  // 開始する
for {
  a <- fa
  b <- fb
} yield (a, b)
```

</details>

<details>
<summary>ヒント3：Future.sequence の使い方</summary>

```scala
val futures: List[Future[Content]] = contentIds.map(id => fetchContent(id))
val result: Future[List[Content]] = Future.sequence(futures)
```

</details>
