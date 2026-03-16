# scala-react-training

## 概要

Scala / React / TypeScript を実践形式で習得するための研修リポジトリ。

**期間**: 2週間
**最終ゴール**: 3つの成果物が連携して動くデモを発表する

---

## デモのイメージ

```
[課題1] Scala CLI
  views.json / likes.json を集計 → contents.json を生成

[課題3] Scala API (port:8080)
  POST /recommend でリクエストを受け取り、レコメンド結果を返す

[課題2] React ダッシュボード
  contents.json を表示 + Scala APIにレコメンドリクエストを送信して結果を表示
```

3つが繋がって動いている状態がゴール。

---

## リポジトリ構成

```
scala-react-training/
├── README.md                        ← ここ
├── docs/                            ← 研修資料（課題・説明・コードリーディング）
│   ├── phase1-scala/                ← Scala基礎〜課題1
│   ├── phase2-react/                ← React/TypeScript基礎〜課題2
│   └── phase3-integration/          ← 3つの成果物を繋げる〜デモ完成
├── src/                             ← 実装コード（デモで見せる成果物）
│   ├── project1-log-aggregator/     ← 課題1（Scala CLI）
│   ├── project2-recommend-api/      ← 課題3（Scala API）
│   └── project3-dashboard/          ← 課題2（React）
└── books/                           ← 使用教材の目次・リファレンスマッピング
```

---

## 進め方

1. `docs/phase1-scala/README.md` を開く
2. step1から順に進める
3. 各stepの完了条件を満たしたら次のstepへ
4. phase1の最後に課題1を完成させる
5. phase2・phase3と同様に進める

**原則**
- 各stepは「動くモジュールが完成したら完了」
- 書籍を調べながら自力で実装する
- 詰まったときはヒントを開く（`<details>` 内）
- AIには実装後のレビューに使う

---

## スケジュール目安

| 期間 | 内容 |
|------|------|
| Week1前半 | phase1-scala（step1〜6 + 課題1） |
| Week1後半〜Week2前半 | phase2-react（step1〜3 + 課題2） |
| Week2後半 | phase3-integration（課題3 + 繋ぎこみ + デモ準備） |

---

## 使用書籍

詳細は `books/README.md` を参照。

| 書籍 | 役割 |
|------|------|
| 実践Scala入門 | Scalaメイン教科書 |
| Scala逆引きレシピ | 詰まったときの辞書 |
| 関数プログラミング実践入門 | 概念理解の補強 |
