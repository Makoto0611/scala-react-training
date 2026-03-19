# Phase6 課題 - ML・DSとの連携実践

**目安時間**: 8〜10時間（step1〜4）
**前提**: Phase4が完了していること（Apollo Client・mock-server.mjs が動く状態）

---

## Step1：機械学習モデルのAPI化とは何か

### 読む箇所

以下の概念を公式ドキュメントや記事で調べて理解する。

- 「機械学習モデルのサービング（serving）」とは何か
- REST API / GraphQL と ML モデルを組み合わせる設計パターン
- 推論エンドポイント（inference endpoint）の役割

### ハンズオン：モデルAPIのレスポンスを読み解く

以下の3つのAPIレスポンスを見て、各フィールドの意味と
フロントエンドとしての表示方針をコメントで書く。

```json
// レスポンス A: セグメントリーチ推定
{
  "segmentId": "seg_001",
  "estimatedReach": 250000,
  "confidence": 0.87,
  "upperBound": 280000,
  "lowerBound": 220000,
  "modelVersion": "v2.3.1",
  "computedAt": "2024-01-15T09:30:00Z"
}
```

```json
// レスポンス B: CTR予測（信頼度が低いケース）
{
  "campaignId": "camp_001",
  "predictedCtr": 0.018,
  "confidence": 0.52,
  "upperBound": 0.031,
  "lowerBound": 0.005,
  "modelVersion": "v1.0.0",
  "computedAt": "2024-01-15T09:30:00Z"
}
```

```json
// レスポンス C: CVR予測（上位モデル）
{
  "segmentId": "seg_002",
  "predictedCvr": 0.025,
  "confidence": 0.93,
  "upperBound": 0.027,
  "lowerBound": 0.023,
  "modelVersion": "v3.1.0",
  "featureImportance": {
    "purchaseHistory": 0.45,
    "carrierData": 0.32,
    "demographics": 0.23
  }
}
```

`docs/phase6-ml-ds-integration/` に `api-analysis.md` を作成し、以下を回答する。

```
Q1: レスポンスBを受け取ったとき、UIにどう表示すべきか。
    confidence: 0.52 はユーザーにどう伝えるか（具体的なUIの文言も考える）

Q2: modelVersion フィールドはなぜ必要か。
    フロントエンドエンジニアとして、バージョン管理をどう活用するか

Q3: featureImportance は何を意味するか。
    「carrierData が 0.32」はどう解釈するか。
    このデータをUI上に表示することに意味があるか

Q4: 3つのレスポンスに共通する設計上の意図は何か
```

### 完了条件

- [ ] `api-analysis.md` が作成されている
- [ ] Q1〜Q4への回答が自分の言葉で書かれている
- [ ] confidence の値に応じたUI表示の判断基準が説明できる

---

## Step2：フロントエンドからモデルAPIを呼び出す実装

### 事前準備：mock-server.mjs の拡張

Phase4 Step3 で作成した `mock-server.mjs` に以下を追記して起動する。

```javascript
// mock-server.mjs の typeDefs に追加
const mlTypeDefs = `
  input SegmentConditions {
    ageRange: [Int!]!
    gender: String
    purchaseCategory: String
    dataSource: String!
  }

  type ReachEstimate {
    estimatedReach: Int!
    confidence: Float!
    upperBound: Int!
    lowerBound: Int!
    modelVersion: String!
  }

  extend type Query {
    estimateSegmentReach(conditions: SegmentConditions!): ReachEstimate!
  }
`

// resolvers に追加
const mlResolvers = {
  Query: {
    estimateSegmentReach: (_, { conditions }) => {
      // dataSource によって信頼度を変える（carrier-audience-data の方が精度が高い想定）
      const isCarrier = conditions.dataSource === 'carrier-audience-data'
      const base = isCarrier ? 250000 : 180000
      const confidence = isCarrier ? 0.87 : 0.61

      return {
        estimatedReach: base,
        confidence,
        upperBound: Math.round(base * 1.12),
        lowerBound: Math.round(base * 0.88),
        modelVersion: isCarrier ? 'v2.3.1' : 'v1.8.0',
      }
    },
  },
}
```

起動確認：
```bash
node mock-server.mjs
# → Server is running on http://localhost:4000/graphql
```

[Apollo Sandbox](https://studio.apollographql.com/sandbox/explorer) で以下のクエリが動くことを確認してから実装に入る。

```graphql
query TestReachEstimate {
  estimateSegmentReach(conditions: {
    ageRange: [25, 44]
    gender: null
    purchaseCategory: "food"
    dataSource: "carrier-audience-data"
  }) {
    estimatedReach
    confidence
    upperBound
    lowerBound
    modelVersion
  }
}
```

### 実装課題：セグメントリーチ推定UI

`src/components/ReachEstimator.tsx` を新規作成する。

**要件：**

```
[ セグメント条件入力 ]
  年齢層（複数選択可）:
    [25-34] [35-44] [45-54] [55-64]

  性別:
    ○ 指定なし  ○ 男性  ○ 女性

  購買カテゴリ:
    [食品▼]  ← セレクトボックス（食品/コスメ/家電/ファッション/スポーツ）

  データソース:
    ○ purchase-data  ○ carrier-audience-data

                    [推定リーチを計算]   ← ボタン（未入力時は disabled）

[ 推定結果 ]（計算前は非表示）
  推定リーチ数: 250,000人
  推定範囲: 220,000〜280,000人

  ⚠ 推定精度が低い可能性があります   ← confidence < 0.7 のときのみ表示
  （信頼度: 61% / モデル: v1.8.0）

  ℹ carrier-audience-data を使うと精度が向上します  ← purchase-data 選択時のみ表示
```

**実装要件（型・ロジック）：**

```typescript
// 型定義（src/types/segment.ts として新規作成）
interface SegmentConditions {
  ageRange: number[]         // 例: [25, 44]
  gender: string | null      // "male" | "female" | null
  purchaseCategory: string
  dataSource: 'purchase-data' | 'carrier-audience-data'
}

interface ReachEstimate {
  estimatedReach: number
  confidence: number
  upperBound: number
  lowerBound: number
  modelVersion: string
}
```

チェックリスト：
- [ ] 年齢層は複数選択できる（ageRange の最小・最大を渡す）
- [ ] `useLazyQuery` を使う（マウント時には実行しない）
- [ ] ボタン押下中は `loading` で disabled にする
- [ ] 数値は `Intl.NumberFormat` でカンマ区切り表示
- [ ] `confidence < 0.7` のとき警告メッセージを表示
- [ ] `purchase-data` 選択時に「carrier-audience-data を使うと精度が向上」を表示
- [ ] `any` を使わない
- [ ] 結果表示エリアは初回未実行時は非表示

### 完了条件

- [ ] mock-server.mjs を拡張して Apollo Sandbox でクエリが通る
- [ ] `ReachEstimator.tsx` が動作する
- [ ] confidence によって表示が切り替わる
- [ ] `src/types/segment.ts` の型定義が使われている
- [ ] `any` を使っていない

---

## Step3：A/Bテストのフロントエンド実装（フィーチャーフラグ）

### 事前準備：モックデータの作成

`public/mock/feature-flags.json` を作成する。

```json
[
  {
    "flagName": "new-reach-estimator-ui",
    "enabled": true,
    "variant": "B",
    "description": "リーチ推定UIの新デザイン（グラフ表示あり）"
  },
  {
    "flagName": "simulation-engine",
    "enabled": false,
    "variant": "A",
    "description": "シミュレーションエンジン（段階的ロールアウト中）"
  }
]
```

### 実装課題：フィーチャーフラグシステム

**① カスタムフックの作成**

`src/hooks/useFeatureFlag.ts` を新規作成する。

```typescript
// 期待するインターフェース
interface FeatureFlag {
  flagName: string
  enabled: boolean
  variant: 'A' | 'B'
  description: string
}

// 使い方
const { variant, loading, error } = useFeatureFlag('new-reach-estimator-ui')
// → variant: "B", loading: false, error: null

// フラグが存在しない場合・取得失敗の場合はデフォルト "A" を返す
```

実装すること：
- `useEffect + fetch` で `/mock/feature-flags.json` を取得
- フラグ名で検索して対象フラグを返す
- 取得失敗時は `variant: "A"` にフォールバック

**② フィーチャーフラグを使ったコンポーネント切り替え**

Step2 で作った `ReachEstimator.tsx` に、UIバリアントの切り替えを追加する。

```
variant: "A"（現行）→ Step2 で実装したフォーム形式のUI
variant: "B"（新UI）→ 結果を棒グラフで表示するUI
                       （グラフは recharts を使う）
```

**variant B の追加要件（グラフ表示）：**

```
[ 推定結果 - グラフ表示 ]
  ┌─────────────────────────────┐
  │                             │
  │  ██████████░░░░░  250,000  │  ← 棒グラフ
  │  |          |    |         │
  │ 220,000  250,000 280,000   │  ← 下限・推定値・上限
  │                             │
  │  信頼度: ████████░░  87%   │  ← プログレスバー
  └─────────────────────────────┘
```

recharts で実装する（Phase2以降でインストール済みのはず）：
```bash
npm install recharts
```

### 確認問題

`docs/phase6-ml-ds-integration/api-analysis.md` に追記する。

```
Q5: フィーチャーフラグの判定を「フロントエンドで行う」場合と
    「バックエンドで行う」場合の違いを説明せよ。
    セキュリティ・パフォーマンス・実装コストの観点で整理すること

Q6: A/Bテストで「統計的に有意な結論」を出すまでフラグを維持したい場合、
    どのくらいの期間・ユーザー数が必要か調べる方法を説明せよ
    （Phase5 Step4 の内容を参照すること）
```

### 完了条件

- [ ] `useFeatureFlag` カスタムフックが動作する
- [ ] variant A / B でUIが切り替わる
- [ ] variant B でグラフが表示される
- [ ] フラグ取得失敗時に A にフォールバックする
- [ ] Q5〜Q6 の回答が書かれている

---

## Step4：シミュレーションエンジンとの連携実装

### 事前準備：mock-server.mjs にMutationを追加

```javascript
// typeDefs に追加
`
  input SimulationInput {
    budget: Float!
    targetSegmentId: String!
    flightDays: Int!
    targetCpa: Float
  }

  type SimulationResult {
    estimatedImpressions: Int!
    estimatedClicks: Int!
    estimatedCv: Int!
    estimatedCpa: Float!
    estimatedCtr: Float!
    estimatedCvr: Float!
    warnings: [String!]!
  }

  extend type Mutation {
    runSimulation(input: SimulationInput!): SimulationResult!
  }
`

// resolvers に追加
Mutation: {
  runSimulation: (_, { input }) => {
    const { budget, flightDays, targetCpa } = input
    const dailyBudget = budget / flightDays
    const cpm = 800  // 想定CPM（円）
    const estimatedImpressions = Math.round((dailyBudget / cpm) * 1000 * flightDays)
    const estimatedClicks = Math.round(estimatedImpressions * 0.01)
    const estimatedCv = Math.round(estimatedClicks * 0.02)
    const estimatedCpa = estimatedCv > 0 ? budget / estimatedCv : 0
    const warnings = []

    if (targetCpa && estimatedCpa > targetCpa) {
      warnings.push(`予測CPA（¥${Math.round(estimatedCpa).toLocaleString()}）が目標CPA（¥${targetCpa.toLocaleString()}）を超えています`)
    }
    if (budget < 100000) {
      warnings.push('予算が10万円未満です。十分なサンプルが集まらない可能性があります')
    }

    return {
      estimatedImpressions,
      estimatedClicks,
      estimatedCv,
      estimatedCpa: Math.round(estimatedCpa),
      estimatedCtr: 0.01,
      estimatedCvr: 0.02,
      warnings,
    }
  },
}
```

### 実装課題：シミュレーションUI

`src/components/SimulationPanel.tsx` を新規作成する。

**要件：**

```
[ シミュレーション条件 ]
  予算: [__________] 円
    ├ 10万  ├ 50万  ├ 100万  ← クイック入力ボタン

  配信期間: [__] 日
    ├ 7日  ├ 14日  ├ 30日   ← クイック入力ボタン

  ターゲットセグメント: [seg_001▼]

  目標CPA: [__________] 円（任意）

            [シミュレーション実行]

[ 予測結果 ]
  ┌──────────────────────────────────────┐
  │ インプレッション  1,200,000          │
  │ クリック数        12,000  (CTR 1.00%)│
  │ CV数              240     (CVR 2.00%)│
  │ 予測CPA           ¥4,167             │
  └──────────────────────────────────────┘

  ⚠ 予測CPAが目標CPAを超えています
  ⚠ 予算が10万円未満です

  [ 条件をリセット ] [ 結果をコピー ]  ← 結果のテキストをクリップボードにコピー
```

**実装要件：**

```typescript
// 型定義（src/types/simulation.ts として新規作成）
interface SimulationInput {
  budget: number
  targetSegmentId: string
  flightDays: number
  targetCpa: number | null
}

interface SimulationResult {
  estimatedImpressions: number
  estimatedClicks: number
  estimatedCv: number
  estimatedCpa: number
  estimatedCtr: number
  estimatedCvr: number
  warnings: string[]
}
```

チェックリスト：
- [ ] `useMutation` を使う
- [ ] クイック入力ボタンで数値がセットされる
- [ ] バリデーション：予算・配信期間は必須・正の数のみ
- [ ] 実行中はボタンを disabled にして「計算中...」表示
- [ ] `warnings` は赤い警告バッジで全件表示
- [ ] 「結果をコピー」で以下のテキストがクリップボードに入る
  ```
  【シミュレーション結果】
  予算: ¥1,000,000 / 14日間
  インプレッション: 1,200,000
  クリック: 12,000 (CTR: 1.00%)
  CV: 240 (CVR: 2.00%)
  予測CPA: ¥4,167
  ```
- [ ] `any` を使わない

### 完了条件

- [ ] mock-server.mjs にMutationが追加され、Apollo Sandboxでクエリが通る
- [ ] `SimulationPanel.tsx` が動作する
- [ ] warnings が表示される（budget: 50000 で試す）
- [ ] クリップボードコピーが動作する
- [ ] `src/types/simulation.ts` の型が使われている

---

## 成果物まとめ

Phase6 完了時点で以下が存在すること：

```
src/
├── components/
│   ├── ReachEstimator.tsx     ← Step2
│   └── SimulationPanel.tsx    ← Step4
├── hooks/
│   └── useFeatureFlag.ts      ← Step3
├── types/
│   ├── segment.ts             ← Step2
│   └── simulation.ts          ← Step4
public/
└── mock/
    └── feature-flags.json     ← Step3
docs/phase6-ml-ds-integration/
├── api-analysis.md            ← Step1・Step3確認問題
```
