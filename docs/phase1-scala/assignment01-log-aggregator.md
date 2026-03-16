# 課題1 - 視聴ログ集計CLIツール

**目安時間**: 6〜8時間
**参照書籍**: 実践Scala入門 第3・4章

step1〜6で学んだ全ての知識を統合して、動く成果物を完成させる。

---

## 仕様

`src/project1-log-aggregator/` に実装する。

### 入力ファイル

`src/project1-log-aggregator/src/main/resources/` に配置する。

**views.json**
```json
[
  {"contentId": "ct_001", "timestamp": 1700000000},
  {"contentId": "ct_001", "timestamp": 1700000010},
  {"contentId": "ct_002", "timestamp": 1700000020},
  {"contentId": "ct_003", "timestamp": 1700000030}
]
```

**likes.json**
```json
[
  {"contentId": "ct_001", "timestamp": 1700000005},
  {"contentId": "ct_002", "timestamp": 1700000025}
]
```

### 期待する出力

```
=== コンテンツパフォーマンスレポート ===
ct_001: views=2, likes=1, likeRate=50.00%
ct_002: views=1, likes=1, likeRate=100.00%
ct_003: views=1, likes=0, likeRate=0.00%
```

出力は contentId の昇順でソートすること。

### 追加仕様（step1〜6で実装したものを統合する）

- データモデルは step1 の case class を使う
- 集計処理は step2 の groupBy / foldLeft を使う
- ゼロ除算は step3 の Either でハンドリングする
- JSONの読み込みは circe ライブラリを使う
- ファイル読み込みエラーは Try でハンドリングする
- 出力後に `contents.json` として集計結果をファイル出力する（phase3の繋ぎこみで使用）

### contents.json の出力仕様

```json
[
  {"contentId": "ct_001", "views": 2, "likes": 1, "likeRate": 50.00},
  {"contentId": "ct_002", "views": 1, "likes": 1, "likeRate": 100.00},
  {"contentId": "ct_003", "views": 1, "likes": 0, "likeRate": 0.00}
]
```

---

## 実装要件

1. circe でJSONを読み込む（`build.sbt` に依存関係を追加する）
2. case class でデータモデルを定義する
3. contentId ごとに視聴・いいね数を集計する
4. いいね率を計算する（Either でゼロ除算を防ぐ）
5. 結果を contentId 昇順にソートして出力する
6. 集計結果を `contents.json` としてファイル出力する

---

## 採点基準

- [ ] `sbt run` で期待する出力と完全一致する
- [ ] `contents.json` が正しいフォーマットで出力される
- [ ] `sbt test` で全テストが通る
- [ ] `null` を使っていない
- [ ] `for` ループ（Javaスタイル）を使っていない
- [ ] ファイル読み込みエラーが適切にハンドリングされている
- [ ] コードが適切にモジュール分割されている（Main.scala に全部書かない）

---

## 詰まったときの調べ方

| 詰まりポイント | 調べる場所 |
|-------------|-----------|
| circe の使い方 | `circe decode Scala` / `circe generic auto` |
| ファイルを文字列として読む | レシピ140〜141 |
| List のソート | `Scala List sortBy` |
| JSON を出力する | `circe encode Scala` |

<details>
<summary>ヒント：circe の依存関係</summary>

build.sbt に以下を追加する。

```scala
libraryDependencies ++= Seq(
  "io.circe" %% "circe-core"    % "0.14.6",
  "io.circe" %% "circe-generic" % "0.14.6",
  "io.circe" %% "circe-parser"  % "0.14.6"
)
```

</details>

<details>
<summary>ヒント：circe の基本的な使い方</summary>

```scala
import io.circe.generic.auto._
import io.circe.parser._

case class View(contentId: String, timestamp: Long)

val json = """[{"contentId":"ct_001","timestamp":1700000000}]"""
val result = decode[List[View]](json)
// Right(List(View(ct_001,1700000000)))
```

</details>
