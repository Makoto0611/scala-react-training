# Step1 - GraphQL の基本概念

**参照リソース**: [graphql.org/learn](https://graphql.org/learn/)
**目安時間**: 1〜1.5時間

---

## 読む箇所

[graphql.org/learn](https://graphql.org/learn/) を開いて以下の節を読む。

- **Queries and Mutations**（クエリとミューテーション）
- **Schemas and Types**（スキーマと型）

読みながら意識すること：
- REST API と何が根本的に違うのか
- `!` がついているフィールドとついていないフィールドの意味
- Query（読み取り）と Mutation（書き込み）の使い分け
- Schema がなぜ重要か

---

## 課題

### Part1：Schema を読んで理解する

以下の Schema を読んで、各問いに答えよ。

```graphql
type Campaign {
  id: ID!
  name: String!
  budget: Float!
  status: String
  report: Report
}

type Report {
  impressions: Int!
  clicks: Int!
  ctr: Float
}

type Query {
  campaign(id: ID!): Campaign
  campaigns: [Campaign!]!
}

type Mutation {
  updateBudget(id: ID!, budget: Float!): Campaign
  deleteCampaign(id: ID!): Boolean!
}
```

```
Q1: Campaign 型の中で、値が必ずあることが保証されているフィールドはどれか。
    「status」と「report」が保証されていないのはなぜか

Q2: campaigns の戻り値 [Campaign!]! を説明せよ。
    [] の中の ! と外の ! はそれぞれ何を意味するか

Q3: updateBudget はなぜ Boolean! ではなく Campaign を返すか
    （ヒント：更新後の値をクライアントにどう伝えるか）
```

### Part2：Query を書いてみる

以下の要件を満たす GraphQL Query を書け（手書きや別ファイルでよい）。

```
要件1: campaigns クエリで、全キャンペーンの id・name・budget だけを取得する

要件2: campaign クエリで、id="ct_001" のキャンペーンの
       name と report（impressions・clicks）を取得する

要件3: 要件2をクエリ変数（variables）で書き直す
       （id をハードコードせず、$id: ID! として渡せるようにする）
```

<details>
<summary>ヒント：クエリ変数の使い方</summary>

```graphql
query GetCampaign($id: ID!) {   # 変数の宣言
  campaign(id: $id) {           # 変数の使用
    name
    budget
  }
}

# 実行時に variables として渡す
# { "id": "ct_001" }
```

</details>

### Part3：Schema の設計を考える

Phase2 で定義した `ContentPerformance` 型を GraphQL Schema で表現せよ。

```typescript
// Phase2 で定義した型（参考）
interface ContentPerformance {
  contentId: string
  views: number
  likes: number
  likeRate: number
}
```

以下を考えて書く：
- 各フィールドの GraphQL 型は何か（`String` / `Int` / `Float` / `ID`）
- `!` はどこに付けるか
- Query の名前・引数・戻り値をどう定義するか

---

## 完了条件

- [ ] `!` の意味が説明できる（nullable と non-nullable の違い）
- [ ] `[Campaign!]!` の読み方が説明できる
- [ ] クエリ変数を使ったクエリが書ける
- [ ] Phase2 の型を GraphQL Schema に変換できる

---

## 詰まったときの調べ方

| 詰まりポイント | 調べる場所 |
|-------------|-----------|
| `!` の意味 | graphql.org/learn「Schemas and Types > Object types and fields」 |
| クエリ変数 | graphql.org/learn「Queries and Mutations > Variables」 |
| GraphQL の型一覧 | graphql.org/learn「Schemas and Types > Scalar types」 |
| Query と Mutation の違い | graphql.org/learn「Queries and Mutations > Mutations」 |

<details>
<summary>ヒント1：GraphQL の基本型</summary>

| GraphQL 型 | TypeScript での対応 | 用途 |
|------------|-------------------|------|
| `String` | `string` | テキスト全般 |
| `Int` | `number`（整数） | 件数・個数 |
| `Float` | `number`（小数） | 割合・金額 |
| `Boolean` | `boolean` | true/false |
| `ID` | `string` | 一意な識別子 |

</details>

<details>
<summary>ヒント2：[Campaign!]! の読み方</summary>

```
[Campaign!]!
 ↑         ↑
 中の !     外の !
 各要素がnullにならない    リスト自体がnullにならない

つまり：
- ✅ []          → OK（空配列は可）
- ✅ [campaign1] → OK
- ❌ null        → NG（リスト自体はnullにならない）
- ❌ [null]      → NG（要素はnullにならない）
```

</details>
