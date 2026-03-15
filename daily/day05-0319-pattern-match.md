# 3/19（木）｜パターンマッチ / sealed trait

**所要目安**: 6〜8時間  
**参照書籍**: 実践Scala入門 第2章「制御構文」「トレイトを定義する」  
**AIなしで取り組む。帰宅後にClaudeと振り返る。**

---

## 今日読む箇所（30分）

実践Scala入門 第2章の `match` 式・トレイト・ケースクラスとの組み合わせ部分を読む。

読みながら意識すること：
- `match` 式は `if / else` と何が違うか
- `sealed trait` にするとコンパイラが何をしてくれるか
- `case object` と `case class` の違い

---

## 課題

### Part 1：sealed trait で広告タイプを定義する

新しいファイル `src/main/scala/models/AdType.scala` を作成する。

```
広告タイプは以下の3種類がある
・バナー広告（Banner）
・動画広告（Video）：再生時間（秒）を持つ
・ネイティブ広告（Native）

これを sealed trait と case class / case object で表現する
どれを case object にして、どれを case class にするか自分で判断する
```

### Part 2：Ad に adType を追加する

`AdModels.scala` の `Ad` case class に `adType: AdType` フィールドを追加する。
既存データ（Main.scala）も更新する。

### Part 3：match 式で処理を分ける

```
関数名： describeAd
引数：   Ad
戻り値： String

・Banner → "[バナー] {広告名} 予算:{予算}円"
・Video  → "[動画/{秒数}s] {広告名} 予算:{予算}円"
・Native → "[ネイティブ] {広告名} 予算:{予算}円"
・予算が 0 の場合はタイプに関わらず "[{タイプ}] {広告名} 予算なし"
```

### Part 4：網羅性チェックを確認する

`sealed` キーワードを外して `sbt compile` してみる。何が変わるか確認して元に戻す。

### Part 5：考察

```scala
// Q1: sealed trait にする理由を具体的に説明せよ
// Q2: case object と case class の使い分け基準は何か
// Q3: ガード条件（if付きのcase）はどこに書くべきか。順番は関係あるか？
```

---

## 完了条件

- [ ] `sealed trait AdType` と各サブタイプが定義されている
- [ ] `describeAd` 関数が動作する
- [ ] 予算0のケースが正しく処理される
- [ ] Part 4 の実験を行った

---

## 詰まったときの調べ方

| 詰まりポイント | 調べるキーワード / 逆引きレシピ番号 |
|-------------|-------------------------------|
| match式 | 実践Scala入門 第2章 / レシピ040 |
| sealed trait | 実践Scala入門 第2章 / レシピ075 |
| ガード条件 | `Scala match guard` / レシピ047 |
| case object vs case class | レシピ086 |
