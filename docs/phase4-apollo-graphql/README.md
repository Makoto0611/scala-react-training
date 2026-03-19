# Phase4 - Apollo GraphQL

## このphaseのゴール

**React から Apollo Client を使って GraphQL API と連携し、
データの取得・更新・エラーハンドリングを実装できること。**

DSP案件の技術スタックに Apollo GraphQL が含まれており、
フロントエンドからバックエンド（Scala API）へのデータ取得に使われる。
「REST API または GraphQL を利用したバックエンド連携の実装経験」（必須スキル）を確実に担保する。

---

## このphaseで身につくこと

- GraphQL の基本概念（Query / Mutation / Schema）を読み書きできる
- Apollo Client を React プロジェクトに導入・設定できる
- `useQuery` でデータを取得してコンポーネントに表示できる
- `useMutation` でデータを更新できる
- loading / error の状態を適切にハンドリングできる
- Phase2のダッシュボードをGraphQL対応に書き換えられる

---

## REST API との違い（前提知識）

以下の比較をよく読んでから step1 に進むこと。

### データ取得のアプローチ

REST APIとGraphQLでは、クライアントがデータを「どう要求するか」が根本的に異なる。

REST APIの場合：
```
GET /campaigns        → キャンペーン全データ（不要なフィールドも全部返ってくる）
GET /campaigns/1      → 特定の1件（やはり全フィールド）
GET /campaigns/1/report → レポートデータ（別のリクエストが必要）

問題：
- 必要なフィールドだけ取れない（Over-fetching）
- 複数リソースを1回で取れない（Under-fetching）
```

GraphQLの場合：
```graphql
query {
  campaign(id: "1") {
    name        # 必要なフィールドだけ指定
    budget
    report {
      impressions
      clicks
    }
  }
}
# 1リクエストで必要なデータだけ取得できる
```

### 型の厳密さ

GraphQL には Schema（型定義）があり、APIが返すデータの形が事前に保証される。
TypeScriptと相性が良く、スキーマから型を自動生成することもできる。

---

## step一覧

| step | テーマ | 参照リソース | 目安時間 |
|------|--------|-------------|---------|
| step1 | GraphQL の基本概念・Schema の読み方 | graphql.org/learn | 1〜1.5時間 |
| step2 | Apollo Client セットアップ・useQuery | Apollo Client 公式 | 1.5〜2時間 |
| step3 | useMutation・エラーハンドリング・キャッシュ | Apollo Client 公式 | 1.5時間 |
| 課題4 | Phase2ダッシュボードを Apollo に書き換える | - | 1.5〜2時間 |

**合計目安: 5.5〜7時間**

---

## 使用するリソース

| リソース | 用途 |
|---------|------|
| [graphql.org/learn](https://graphql.org/learn/) | GraphQL の概念理解 |
| [Apollo Client 公式（React）](https://www.apollographql.com/docs/react/) | セットアップ・APIリファレンス |
| [Apollo Sandbox](https://studio.apollographql.com/sandbox/explorer) | ブラウザでGraphQLを試せる演習環境 |

---

## 完了条件（phase4 全体）

- [ ] Query / Mutation / Schema の読み方が理解できている
- [ ] ApolloProvider のセットアップができる
- [ ] `useQuery` でデータを取得してコンポーネントに表示できる
- [ ] `useMutation` でデータを更新できる
- [ ] loading / error の状態を適切にハンドリングできる
- [ ] Phase2ダッシュボードをGraphQL版に書き換えて動く

---

## DSP案件との接続イメージ

案件のフロントエンドでは以下のような構造が想定される。

```
React（フロントエンド）
  └── Apollo Client
        └── useQuery / useMutation
              └── Scala API（Play Framework）
                    └── BigQuery / Databricks（データ取得）
```

このphaseを終えることで、案件参画初日にこの構造を読んで
「どこに何を実装するか」がすぐにわかるようになる。

---

## ファイル構成（参考）

```
src/
├── main.tsx                 ← ApolloProvider を追加する
├── lib/
│   └── apolloClient.ts      ← Apollo Client の設定
├── graphql/
│   ├── queries.ts           ← gql`` で書いた Query を管理
│   └── mutations.ts         ← gql`` で書いた Mutation を管理
└── components/
    └── CampaignList.tsx     ← useQuery を使うコンポーネント
```
