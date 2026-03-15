# 3/18（水）｜コレクション操作：fold / groupBy

**所要目安**: 6〜8時間  
**参照書籍**: 実践Scala入門 第4章「コレクションを操作するAPI」「for式によるコレクション操作」  
**AIなしで取り組む。帰宅後にClaudeと振り返る。**

---

## 今日読む箇所（30分）

実践Scala入門 第4章の `foldLeft` / `groupBy` / `Map操作` 部分を読む。

読みながら意識すること：
- `foldLeft` の引数は何か（初期値・関数）
- `groupBy` の戻り値の型は何か
- `Map` の操作方法（get / getOrElse / keys）

---

## 課題

```scala
val impressions = List(
  Impression("ad_001", 1700000000L),
  Impression("ad_001", 1700000010L),
  Impression("ad_002", 1700000020L),
  Impression("ad_003", 1700000030L)
)
val clicks = List(
  Click("ad_001", 1700000005L),
  Click("ad_002", 1700000025L)
)
```

### Part 1：foldLeft で集計

`for` ループ・`size` メソッドは使わない。`foldLeft` だけで書く。

```
1. impressions の総件数をカウントする
2. clicks の総件数をカウントする
3. impressions の timestamp の合計を求める（練習）
```

### Part 2：groupBy でグループ化

```
1. impressions を adId ごとにグループ化 → Map[String, List[Impression]]
2. clicks を adId ごとにグループ化 → Map[String, List[Click]]
3. 各 adId のインプレッション件数を Map で作る
   期待値： Map("ad_001" -> 2, "ad_002" -> 1, "ad_003" -> 1)
```

### Part 3：Map を組み合わせてCTRを計算する

```
=== 集計結果 ===
ad_001: impressions=2, clicks=1
ad_002: impressions=1, clicks=1
ad_003: impressions=1, clicks=0
```

`ad_003` はクリックデータが存在しないため `0` になる。
`clicks` の Map に存在しない `adId` をどう扱うか自分で考える。

### Part 4：考察

```scala
// Q1: foldLeft の第1引数（初期値）を間違えるとどうなるか？
// Q2: groupBy の戻り値が Map[String, List[...]] になる理由を説明せよ
// Q3: getOrElse を使う理由は何か。get だけではなぜダメか？
```

---

## 完了条件

- [ ] Part 1 が `foldLeft` だけで動作する
- [ ] Part 2 の3つが動作する
- [ ] Part 3 の出力が正しい（ad_003 が 0）
- [ ] Part 4のコメントが書かれている

---

## 詰まったときの調べ方

| 詰まりポイント | 調べるキーワード / 逆引きレシピ番号 |
|-------------|-------------------------------|
| foldLeft | 実践Scala入門 第4章 / レシピ120 |
| groupBy | `Scala groupBy` / レシピ124 |
| Mapの存在しないキー | `Scala Map getOrElse` / レシピ123 |
