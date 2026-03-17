# Step2 Part1 レビュー

**日時**: 2026年03月17日（Tuesday）17時43分 JST

---

## 完成度

| 問 | 内容 | 結果 |
|---|---|---|
| 1 | `views.filter` で ct_001 のみ抽出 | ✅ |
| 2 | `views.map` で id一覧を取得 | ✅ |
| 3 | `contains` で contents に存在する視聴ログのみ抽出 | ✅ |
| 4 | `filter` + `map` で再生時間1800秒以上のタイトル一覧 | ✅ |
| 5 | `foldLeft` で再生時間合計 | ✅ |

---

## よくできた点

- `foldLeft` の概念を図解で理解してから自力で実装できた
- モデルファイルを自分で確認してフィールド名の誤りを指摘できた（`v.contentId` → `v.id`）
- 変数をワンライナーにまとめず分けて書く理由を理解した

---

## 改善点・注意事項

- `List<String>`（Java記法）と `List[String]`（Scala記法）の混在に注意
- `filter` の対象リストを間違えるミスが多い。課題文を丁寧に読む習慣をつける
- `==` と `=` の区別（比較 vs 代入）を意識する

---

## Part2 への引き継ぎ

`groupBy` の返り値の型は `Map[String, List[View]]`。

```
Map(
  "ct_001" -> List(view1, view2),
  "ct_002" -> List(view3),
  "ct_003" -> List(view4)
)
```

`groupBy` 自体の書き方（`views.groupBy(v => v.id)`）は既に書けている。
次は各 contentId の**件数**を取り出す操作（`mapValues`）と `getOrElse` を学ぶ。
