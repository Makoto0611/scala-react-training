# Step1 - データモデル設計と実装

**参照書籍**: 実践Scala入門 第2章
**目安時間**: 4〜6時間

---

## 読む箇所（先に読んでから実装に入ること）

実践Scala入門 第2章を以下の節を読む。

- 基本的な型
- クラスを定義する
- トレイトを定義する
- ケースクラス（メソッドを自動生成する）
- Scalaにおけるstatic
- 制御構文
- ジェネリクスと型パラメータ

補助として「関数プログラミング実践入門」第1章「構造化データの取り扱い」も読むと、
なぜ case class を使うのかの背景が理解できる。

---

## 課題

動画配信システムで扱う以下のデータモデルを、Scalaで設計・実装する。

### 実装するもの

`src/main/scala/models/` 配下に以下を実装する。

**ContentModels.scala**

以下の3種類のデータを case class で表現する。
フィールド名・型は自分で設計すること（ただし要件を満たすこと）。

```
コンテンツ（Content）
  - コンテンツを一意に識別できる
  - タイトルを持つ
  - 再生時間（秒）を持つ

視聴ログ（View）
  - どのコンテンツが視聴されたかを識別できる
  - いつ視聴されたか（Unix時間）を持つ

いいねログ（Like）
  - どのコンテンツにいいねされたかを識別できる
  - いついいねされたか（Unix時間）を持つ
```

**ContentType.scala**

コンテンツには以下の3種類がある。sealed trait と case class / case object で表現する。
どれを case class にして、どれを case object にするかは自分で判断すること。

```
映画（Movie）
シリーズ（Series）  → シーズン数を持つ
ライブ配信（Live）
```

Content の case class に contentType フィールドを追加すること。

### 動作確認

`src/main/scala/Main.scala` を修正し、
`sbt run` で以下の出力が出ることを確認する。

```
=== コンテンツ一覧 ===
ct_001 | サンプル映画A | 映画 | 再生時間:5400秒
ct_002 | サンプルシリーズB | シリーズ(3シーズン) | 再生時間:3600秒
ct_003 | サンプルライブC | ライブ | 再生時間:0秒

=== 視聴ログ ===
ct_001 | 1700000000
ct_001 | 1700000010
ct_002 | 1700000020
ct_003 | 1700000030

=== いいねログ ===
ct_001 | 1700000005
ct_002 | 1700000025
```

**制約**
- `for` ループを使わない
- `null` を使わない
- `s"..."` 構文（文字列補間）を使う
- シリーズのシーズン数は出力に含める

---

## 完了条件

- [ ] `sbt run` で期待する出力が出る
- [ ] sealed trait で ContentType が定義されている
- [ ] `for` ループを使っていない
- [ ] `null` を使っていない
- [ ] Content に contentType フィールドがある

---

## 詰まったときの調べ方

| 詰まりポイント | 調べる場所 |
|-------------|-----------|
| case class の基本 | 実践Scala入門 第2章「ケースクラス」 |
| sealed trait の使い方 | 実践Scala入門 第2章「トレイトを定義する」/ レシピ075 |
| case object vs case class | レシピ086 |
| パターンマッチ | 実践Scala入門 第2章「制御構文」/ レシピ040〜049 |
| for を使わずにリストを出力 | `Scala List foreach` |
| s"..." の書き方 | 実践Scala入門 第2章「基本的な型」 |

<details>
<summary>ヒント1：sealed trait を使う理由</summary>

sealed にするとコンパイラがパターンマッチの網羅性チェックをしてくれる。
つまり、どれかの case を書き忘れるとコンパイルエラーになる。
意図していないケースを実行時に見逃すリスクがなくなる。

</details>

<details>
<summary>ヒント2：case object と case class の使い分け</summary>

フィールドを持たないものは case object を使う。
フィールドを持つものは case class を使う。
Series はシーズン数を持つので case class になる。

</details>

<details>
<summary>ヒント3：foreach の使い方</summary>

```scala
list.foreach(item => println(item))
// または
list.foreach(println)
```

</details>
