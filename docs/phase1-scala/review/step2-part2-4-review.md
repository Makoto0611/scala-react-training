# Step2 Part2〜4 レビュー

**日時**: 2026年03月19日（木）13時36分 JST

---

## 完成度

| Part | 内容 | 結果 |
|------|------|------|
| Part2-1 | `groupBy` で views を contentId ごとにグループ化 | ✅ |
| Part2-2 | `groupBy` で likes を contentId ごとにグループ化 | ✅ |
| Part2-3 | `mapValues(_.size)` で視聴件数 Map を作成 | ✅ |
| Part2-4 | `mapValues(_.size)` でいいね件数 Map を作成 | ✅ |
| Part3 | `getOrElse` でゼロ除算を回避しいいね率を出力 | ✅ |
| Part4 | `for式` で Part3 と同じ出力を再現 | ✅ |

---

## よくできた点

- `groupBy` の返り値 `Map[String, List[View]]` を正確に型注釈で書けている
- `mapValues(_.size).toMap()` のチェーンが適切で、簡潔にまとめられている
- Part3 の `getOrElse(c.id, 0)` で ct_003 のようにいいねデータが存在しないケースを正しくハンドリングできている
- `lc.toDouble / vc * 100` のゼロ除算回避を `if (vc == 0)` で明示的に書いており、意図が読みやすい
- Part4 の for式が `for (c <- contents)` と読みやすく書けており、Part3 の `foreach` との対比を確認できる形になっている

---

## 改善点・注意事項

### `mapValues` の Deprecation について

```scala
// 現在の実装
val viewCount: Map[String, Int] = viewGroup.mapValues(_.size).toMap()
```

Scala 2.13 以降、`mapValues` は `view.mapValues` が推奨されている。  
今回は `.toMap()` で実体化しているため動作上の問題はないが、将来的には以下の書き方も覚えておくとよい。

```scala
val viewCount: Map[String, Int] = viewGroup.map { case (k, v) => k -> v.size }
```

### Part4：for式と foreach の使い分け

今回の `for (c <- contents) { ... }` は実質 `foreach` と同じ動作。  
for式の本来の力は **値を返す（yield）** ときに発揮される。

```scala
// yield を使うと新しいコレクションを生成できる
val results = for (c <- contents) yield {
  val vc = viewCount.getOrElse(c.id, 0)
  s"${c.id}: views=${vc}"
}
```

Step3 で `Option` と組み合わせると、for式がより重要になってくる。

---

## Step3 への引き継ぎ

- `getOrElse` の感覚は Step3 の `Option` に直結する（`None` のときのデフォルト値を返す）
- `viewCount.getOrElse(c.id, 0)` は「値がないかもしれない Map の参照」であり、`Option` の考え方と同じ
- for式は Step3 で `Option` / `Either` と組み合わせた for-comprehension として登場する
