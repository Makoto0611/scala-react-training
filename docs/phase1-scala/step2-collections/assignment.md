# Step2 - コレクション操作

**参照書籍**: 実践Scala入門 第4章
**目安時間**: 4〜6時間

---

## 読む箇所

実践Scala入門 第4章を以下の節を読む。

- コレクションのデータ型
- コレクションを操作するAPI（map / filter / flatMap / foldLeft / groupBy）
- for式によるコレクション操作

補助として「関数プログラミング実践入門」第3章「高階関数」を読むと、
なぜループではなく map/filter/fold で書くのかの背景が理解できる。

---

## 課題

step1で定義した Impression / Click / Ad を使って集計処理を実装する。

`src/project1-log-aggregator/src/main/scala/aggregator/` を作成し、
以下の処理をすべて実装する。

### Part1：基本的なコレクション操作

以下のデータを使って実装する。

```scala
val impressions: List[Impression] = // step1で定義したものを使う
val clicks: List[Click] = // step1で定義したものを使う
val ads: List[Ad] = // step1で定義したものを使う
```

実装する処理：

```
1. impressions から adId が "ad_001" のものだけ抽出する
2. impressions の全 adId を List にする（重複あり）
3. ads に存在する adId のインプレッションのみ抽出する（ad_003はadsにないと仮定）
4. 予算が 10000円以上の広告名だけの List を作る
5. 予算の合計を foldLeft で計算する（size・sum は使わない）
```

### Part2：groupBy で集計する

```
1. impressions を adId ごとにグループ化する
   → 型は Map[String, List[Impression]] になる

2. clicks を adId ごとにグループ化する
   → 型は Map[String, List[Click]] になる

3. 各 adId のインプレッション件数を Map にする
   → 期待値：Map("ad_001" -> 2, "ad_002" -> 1, "ad_003" -> 1)

4. 各 adId のクリック件数を Map にする
   → ad_003 はクリックデータが存在しないため 0 になる（getOrElse を使う）
```

### Part3：CTR を計算して出力する

```
=== 集計結果 ===
ad_001: impressions=2, clicks=1, CTR=50.00%
ad_002: impressions=1, clicks=1, CTR=100.00%
ad_003: impressions=1, clicks=0, CTR=0.00%
```

ゼロ除算が発生しないようにすること。
出力フォーマットは上記に完全一致させること。

### Part4：for式で書き直す

Part3 の集計処理を for式（for comprehension）で書き直したバージョンを作り、
同じ出力が出ることを確認する。

---

## 完了条件

- [ ] Part1の5つの処理が動作する
- [ ] Part2の4つの処理が動作する
- [ ] Part3の出力が期待値と完全一致する（ad_003のCTRが0.00%になっている）
- [ ] Part4がfor式で書かれていて同じ出力が出る
- [ ] `for` ループ（Javaスタイル）を使っていない
- [ ] `null` を使っていない

---

## 詰まったときの調べ方

| 詰まりポイント | 調べる場所 |
|-------------|-----------|
| filter の使い方 | 実践Scala入門 第4章 / レシピ118 |
| map の使い方 | 実践Scala入門 第4章 / レシピ113 |
| foldLeft の使い方 | 実践Scala入門 第4章 / レシピ120 |
| groupBy の使い方 | 実践Scala入門 第4章 / レシピ124 |
| Map の存在しないキー | `Scala Map getOrElse` / レシピ123 |
| for式の書き方 | 実践Scala入門 第4章「for式によるコレクション操作」 |
| 小数点2桁のフォーマット | `Scala String format` または `f"$value%.2f"` |

<details>
<summary>ヒント1：foldLeft の構造</summary>

```scala
list.foldLeft(初期値)((累積値, 要素) => 次の累積値)
// 例：合計
List(1, 2, 3).foldLeft(0)((acc, x) => acc + x) // 6
```

</details>

<details>
<summary>ヒント2：存在しないキーの扱い</summary>

```scala
val m = Map("a" -> 1)
m.get("b")           // None
m.getOrElse("b", 0)  // 0
```

</details>

<details>
<summary>ヒント3：ゼロ除算の回避</summary>

impressions が 0 のとき CTR は 0.00% にする。
if式で条件分岐するか、Option を使う方法がある。

</details>
