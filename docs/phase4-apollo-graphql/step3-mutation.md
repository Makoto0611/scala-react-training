# Step3 - useMutation・エラーハンドリング・キャッシュ

**参照リソース**: [Apollo Client 公式 - Mutations](https://www.apollographql.com/docs/react/data/mutations)
**目安時間**: 1.5時間

---

## 読む箇所

Apollo Client 公式ドキュメントの以下を読む。

- **Mutations** → useMutation の基本・変数の渡し方
- **Mutations > Refetching queries** → 更新後のデータ再取得
- **Error handling** → networkError と graphQLErrors の違い

---

## 課題

### Part1：useQuery と useMutation の違いを理解する

以下の2つのコードを比べて、問いに答えよ。

```typescript
// パターンA: useQuery
const { loading, error, data } = useQuery(GET_CAMPAIGNS)

// パターンB: useMutation
const [updateBudget, { loading, error, data }] = useMutation(UPDATE_BUDGET)
```

```
Q1: useQuery はコンポーネントのマウント時にすぐ実行される。
    useMutation はなぜ「関数 + 結果オブジェクト」のタプルを返すのか

Q2: useMutation の loading は「いつ true になって、いつ false になるか」

Q3: updateBudget（ミューテーション関数）はどのタイミングで呼ぶか
    （コンポーネントのマウント時？ボタン押下時？）
```

### Part2：ローカルのモックサーバーを立てる

演習用に GraphQL モックサーバーを使う。

**インストール**

```bash
npm install -D graphql-yoga
```

プロジェクトルートに `mock-server.mjs` を作成する：

```javascript
import { createServer } from 'graphql-yoga'

const campaigns = [
  { id: '1', name: 'キャンペーンA', budget: 100000, status: 'active' },
  { id: '2', name: 'キャンペーンB', budget: 50000, status: 'paused' },
  { id: '3', name: 'キャンペーンC', budget: 200000, status: 'active' },
]

const typeDefs = `
  type Campaign {
    id: ID!
    name: String!
    budget: Float!
    status: String!
  }
  type Query {
    campaigns: [Campaign!]!
  }
  type Mutation {
    updateBudget(id: ID!, budget: Float!): Campaign
  }
`

const resolvers = {
  Query: {
    campaigns: () => campaigns,
  },
  Mutation: {
    updateBudget: (_, { id, budget }) => {
      const campaign = campaigns.find(c => c.id === id)
      if (!campaign) return null
      campaign.budget = budget
      return campaign
    },
  },
}

const server = createServer({ typeDefs, resolvers })
server.start().then(() => console.log('Server is running on http://localhost:4000/graphql'))
```

起動：

```bash
node mock-server.mjs
```

`apolloClient.ts` の `uri` を `http://localhost:4000/graphql` に変更する。

### Part3：useMutation を実装する

`src/components/CampaignList.tsx` を新規作成する。

```
要件:
- useQuery で全キャンペーン（id・name・budget・status）を取得してリスト表示する
- 各キャンペーンに「予算を更新」ボタンを設ける
- ボタン押下時、prompt で新しい予算を入力させ useMutation で更新する
- 更新中はボタンを disabled にする
- 更新後はキャンペーン一覧を自動的に再取得して表示を更新する
- error 時にエラーメッセージを表示する
- TypeScript の型を適切に定義する（any を使わない）
```

<details>
<summary>ヒント：useMutation の基本形</summary>

```typescript
const UPDATE_BUDGET = gql`
  mutation UpdateBudget($id: ID!, $budget: Float!) {
    updateBudget(id: $id, budget: $budget) {
      id
      budget
    }
  }
`

const [updateBudget, { loading: updating, error }] = useMutation(UPDATE_BUDGET, {
  refetchQueries: [{ query: GET_CAMPAIGNS }],  // 更新後に自動再フェッチ
})

// 呼び出し方
await updateBudget({
  variables: { id: campaign.id, budget: newBudget }
})
```

</details>

<details>
<summary>ヒント：ボタンの disabled 制御</summary>

```typescript
<button
  onClick={handleUpdate}
  disabled={updating}
>
  {updating ? '更新中...' : '予算を更新'}
</button>
```

`useMutation` の `loading`（ここでは `updating` と命名）を使う。
`useQuery` の `loading` と区別するため、分割代入時に別名をつけると混乱しない。

</details>

### Part4：エラーハンドリングのパターンを確認する

以下の3つのパターンをそれぞれ試して動作確認する。

```
パターン1: ネットワークエラー
  → モックサーバーを停止した状態で useQuery を実行してみる
  → error.networkError に値が入ることを確認する

パターン2: 存在しない ID での更新
  → モックサーバーに存在しない id（例: "999"）で updateBudget を実行してみる
  → サーバー側で null を返す場合の挙動を確認する

パターン3: 更新成功 → 再フェッチ
  → updateBudget 成功後、一覧が自動的に更新されることを確認する
  → ネットワークタブで GET_CAMPAIGNS が再発行されていることを確認する
```

確認問題：

```
Q1: networkError と graphQLErrors の違いは何か

Q2: refetchQueries を使う方法と Apollo のキャッシュを直接更新する方法（update オプション）は
    どう使い分けるか。それぞれどんな場面に向いているか
```

---

## 完了条件

- [ ] useQuery と useMutation の返り値の違いが説明できる
- [ ] `useMutation` でデータを更新できる
- [ ] 更新中はボタンが disabled になる
- [ ] `refetchQueries` で更新後に一覧が再取得される
- [ ] networkError と graphQLErrors の違いが説明できる
- [ ] `any` を使っていない

---

## 詰まったときの調べ方

| 詰まりポイント | 調べる場所 |
|-------------|-----------|
| useMutation の基本 | [公式: Mutations](https://www.apollographql.com/docs/react/data/mutations) |
| refetchQueries | [公式: Mutations > Refetching queries](https://www.apollographql.com/docs/react/data/mutations#refetching-queries) |
| エラーの種類 | [公式: Error handling](https://www.apollographql.com/docs/react/data/error-handling) |
| キャッシュの直接更新 | [公式: update function](https://www.apollographql.com/docs/react/data/mutations#the-update-function) |

<details>
<summary>ヒント：loading の変数名衝突を避ける</summary>

useQuery と useMutation の両方が loading を返すので、同じコンポーネント内で使う場合は
分割代入時に別名をつける。

```typescript
const { loading: fetching, data } = useQuery(GET_CAMPAIGNS)
const [updateBudget, { loading: updating }] = useMutation(UPDATE_BUDGET)
```

</details>
