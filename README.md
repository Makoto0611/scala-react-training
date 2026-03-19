# scala-react-training

## 概要

Scala / React / TypeScript / Apollo GraphQL を実践形式で習得するための研修リポジトリ。

**期間**: 2週間（残り10日）+ 研修後キャッチアップ
**最終ゴール**: 3つの成果物が連携して動くデモ + DSP案件参画に必要なスキルを習得する

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
├── build.sbt                        ← sbtビルド設定
├── docs/
│   ├── phase1-scala/                ← Scala基礎〜課題1
│   ├── phase2-react/                ← React/TypeScript基礎〜課題2
│   ├── phase3-integration/          ← 3つの成果物を繋げる〜デモ完成
│   ├── phase4-apollo-graphql/       ← Apollo GraphQL（DSP案件 高スキル対応）
│   ├── phase5-data-platform/        ← データ基盤・統計・DS連携（DSP案件 中スキル対応）
│   ├── phase6-ml-ds-integration/    ← ML・DSとの連携実践（研修後キャッチアップ）
│   └── phase7-data-engineering/     ← データエンジニアリング実践（研修後キャッチアップ）
├── src/
│   └── main/
│       └── scala/
│           ├── Main.scala
│           └── models/
│               ├── ContentType.scala
│               └── ContentModels.scala
└── books/
```

---

## 進め方

1. `docs/phase1-scala/README.md` を開く
2. step1から順に進める
3. 各stepの完了条件を満たしたら次のstepへ
4. phase1の最後に課題1を完成させる
5. phase2〜phase7と同様に進める

**原則**
- 各stepは「動くモジュールが完成したら完了」
- 書籍を調べながら自力で実装する
- 詰まったときはヒントを開く（`<details>` 内）
- AIには実装後のレビューに使う

---

## スケジュール目安

### 研修10日間（1日8.9時間）

| 日程 | Phase | 内容 | 目安時間 |
|------|-------|------|---------|
| Day1〜2 | phase1 | Scala Step2 Part4 〜 Step6 | 〜15時間 |
| Day3 | phase1 | 課題1（Scala CLI 統合） | 6〜8時間 |
| Day4〜5 | phase2 | React Step1〜3 | 〜15時間 |
| Day6 | phase2 | 課題2（ダッシュボード） | 6〜8時間 |
| Day7〜8 | phase3 | 課題3 + 統合 + デモ準備 | 〜15時間 |
| Day9 | phase4 | Apollo GraphQL（Step1〜3 + 課題4） | 4〜5時間 |
| Day10 | phase5 | データ基盤・統計・DS連携（Step1〜4） | 4〜5時間 |

**合計目安: 89時間（10日 × 8.9時間）**

### 研修後キャッチアップ（自己学習）

| Phase | 内容 | 優先度 | 目安時間 |
|-------|------|--------|---------|
| phase6 | ML・DSとの連携実践 | A（参画直後） | 5時間 |
| phase7 | データエンジニアリング実践 | B（参画1〜2ヶ月） | 6〜7時間 |

---

## DSP案件スキルマッピング

| JDの要件 | 優先度 | 対応するPhase |
|---------|------|-------------|
| TypeScript SPA開発 | 高 | phase2 |
| REST API または GraphQL 連携 | 高 | phase4（Apollo GraphQL） |
| Scala / JVM系サーバーサイド | 中 | phase1 + phase3 |
| 管理画面・ダッシュボード開発 | 中 | phase2（職歴でもカバー済み） |
| クラウドDWH（BigQuery等） | 中 | phase5（Airflow学習で基礎済み） |
| Databricks 概念理解 | 中 | phase5 |
| 統計学・A/Bテスト概念 | 中 | phase5 |
| ML/DSとの協業・モデル組み込み | 中 | phase6 |
| ETLパイプライン運用 | 中 | phase7（Airflow学習ベース） |
| SQL大規模集計 | 中 | phase7 |
| dbt 概念理解 | 中 | phase7 |

---

## 使用書籍

詳細は `books/README.md` を参照。

| 書籍 | 役割 |
|------|------|
| 実践Scala入門 | Scalaメイン教科書 |
| Scala逆引きレシピ | 詰まったときの辞書 |
| 関数プログラミング実践入門 | 概念理解の補強 |
