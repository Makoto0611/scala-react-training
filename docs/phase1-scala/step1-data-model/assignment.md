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

DSPの広告配信システムで扱う以下のデータモデルを、Scalaで設計・実装する。

### 実装するもの

`src/project1-log-aggregator/src/main/scala/models/` 配下に以下を実装する。

**AdModels.scala**

以下の3種類のデータを case class で表現する。
フィールド名・型は自分で設計すること（ただし要件を満たすこと）。

```
広告（Ad）
  - 広告を一意に識別できる
  - 広告名を持つ
  - 予算（円）を持つ

インプレッション（Impression）
  - どの広告が表示されたかを識別できる
  - いつ表示されたか（Unix時間）を持つ

クリック（Click）
  - どの広告がクリックされたかを識別できる
  - いつクリックされたか（Unix時間）を持つ
```

**AdType.scala**

広告には以下の3種類がある。sealed trait と case class / case object で表現する。
どれを case class にして、どれを case object にするかは自分で判断すること。

```
バナー広告（Banner）
動画広告（Video）  → 再生時間（秒）を持つ
ネイティブ広告（Native）
```

Ad の case class に adType フィールドを追加すること。

### 動作確認

`src/project1-log-aggregator/src/main/scala/Main.scala` を修正し、
`sbt run` で以下の出力が出ることを確認する。

```
=== 広告マスター ===
ad_001 | テスト広告A | バナー | 予算:50000円
ad_002 | テスト広告B | 動画(30s) | 予算:30000円
ad_003 | テスト広告C | ネイティブ | 予算:0円

=== インプレッションログ ===
ad_001 | 1700000000
ad_001 | 1700000010
ad_002 | 1700000020
ad_003 | 1700000030

=== クリックログ ===
ad_001 | 1700000005
ad_002 | 1700000025
```

**制約**
- `for` ループを使わない
- `null` を使わない
- `s"..."` 構文（文字列補間）を使う
- 動画広告の秒数は出力に含める

---

## 完了条件

- [ ] `sbt run` で期待する出力が出る
- [ ] sealed trait で AdType が定義されている
- [ ] `for` ループを使っていない
- [ ] `null` を使っていない
- [ ] Ad に adType フィールドがある

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
Video は秒数を持つので case class になる。

</details>

<details>
<summary>ヒント3：foreach の使い方</summary>

```scala
list.foreach(item => println(item))
// または
list.foreach(println)
```

</details>
