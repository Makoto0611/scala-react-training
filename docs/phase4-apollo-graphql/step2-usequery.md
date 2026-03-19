# Step2 - Apollo Client セットアップ・useQuery

**参照リソース**: [Apollo Client 公式（React）](https://www.apollographql.com/docs/react/)
**目安時間**: 1.5〜2時間

---

## 読む箇所

[Apollo Client 公式ドキュメント](https://www.apollographql.com/docs/react/) の以下を読む。

- **Get started** → セットアップの全体像を把握する
- **Queries** → useQuery の使い方・オプション・型定義

---

## 課題

### Part1：Apollo Client のセットアップ

Phase2 の React プロジェクトに Apollo Client を導入する。

**インストール**

```bash
npm install @apollo/client graphql
```

**`src/lib/apolloClient.ts` を新規作成する**

ApolloClient のインスタンスを作成する。
`uri` と `cache` の2つが最低限必要。それぞれ何を設定するか公式ドキュメントで確認すること。

<details>
<summary>ヒント：apolloClient.ts の基本形</summary>

```typescript
import { ApolloClient, InMemoryCache } from '@apollo/client'

export const client = new ApolloClient({
  uri: 'https://???',        // ← Part2 で確認する
  cache: new InMemoryCache(),
})
```

`InMemoryCache` はApolloがデータをキャッシュするための仕組み。
同じクエリを2回実行したとき、2回目はネットワークを使わずキャッシュから返してくれる。

</details>

**`src/main.tsx` に ApolloProvider を追加する**

公式ドキュメントの「Get started」を読んで、`<App />` を `ApolloProvider` でラップする。
どこに追加するかを自分で判断すること。

<details>
<summary>ヒント：ApolloProvider の追加場所</summary>

```typescript
// src/main.tsx
import { ApolloProvider } from '@apollo/client'
import { client } from './lib/apolloClient'

ReactDOM.createRoot(document.getElementById('root')!).render(
  <ApolloProvider client={client}>
    <App />
  </ApolloProvider>
)
```

`ApolloProvider` は `<App />` の外側でラップする。
これにより、アプリ全体の任意のコンポーネントで `useQuery` / `useMutation` が使えるようになる。
React の Context の仕組みと同じ考え方。

</details>

### Part2：演習用の GraphQL サーバーを使う

[Apollo Sandbox](https://studio.apollographql.com/sandbox/explorer) にアクセスし、
以下の公開GraphQL APIを接続先として動作確認する。

```
演習用エンドポイント（無料の公開API）:
https://countries.trevorblades.com/
```

サンドボックスで以下のクエリを実行してみる：

```graphql
query {
  countries {
    code
    name
    capital
  }
}
```

確認すること：
- クエリの実行結果がJSON形式で返ってくる
- `code` だけに変えるとレスポンスが変わる
- 存在しないフィールドを追加するとエラーになる

`apolloClient.ts` の `uri` をこのエンドポイントに変更する。

### Part3：useQuery でデータを取得する

`src/components/CountryList.tsx` を新規作成する。

```
要件:
- useQuery を使って全国の一覧（code・name・capital）を取得する
- loading 中は「読み込み中...」を表示する
- error 時は「データの取得に失敗しました: [エラーメッセージ]」を表示する
- 取得した国の一覧を ul/li でリスト表示する
- TypeScript の型を適切に定義する（any を使わない）
```

実装後、`App.tsx` に `<CountryList />` を追加して `localhost:5173` で動作確認する。

<details>
<summary>ヒント：useQuery の基本形</summary>

```typescript
import { useQuery, gql } from '@apollo/client'

const GET_COUNTRIES = gql`
  query GetCountries {
    countries {
      code
      name
      capital
    }
  }
`

// Apollo が返すデータの型を定義する
interface Country {
  code: string
  name: string
  capital: string | null  // capital は null の場合がある
}

interface GetCountriesData {
  countries: Country[]
}

function CountryList() {
  const { loading, error, data } = useQuery<GetCountriesData>(GET_COUNTRIES)

  if (loading) return <p>読み込み中...</p>
  if (error) return <p>データの取得に失敗しました: {error.message}</p>

  return (
    <ul>
      {data?.countries.map((country) => (
        <li key={country.code}>
          {country.name}（{country.capital ?? '首都なし'}）
        </li>
      ))}
    </ul>
  )
}
```

</details>

### Part4：variables を使ってフィルタリングする

以下のクエリを使って、1カ国の詳細を取得するコンポーネント `CountryDetail.tsx` を実装する。

```graphql
query GetCountry($code: ID!) {
  country(code: $code) {
    name
    capital
    currency
    languages {
      name
    }
  }
}
```

```
要件:
- props で code（例: "JP"）を受け取る
- useQuery の variables に code を渡す
- 国名・首都・通貨・言語一覧を表示する
- loading / error を適切にハンドリングする
```

<details>
<summary>ヒント：variables の渡し方</summary>

```typescript
// useQuery の第2型引数に「variables の型」を指定する
const { loading, error, data } = useQuery<GetCountryData, { code: string }>(
  GET_COUNTRY,
  { variables: { code } }  // props から受け取った値を渡す
)
```

型を指定することで、variables の typo や型ミスをコンパイル時に検出できる。

</details>

---

## 完了条件

- [ ] ApolloProvider が `main.tsx` でセットアップされている
- [ ] `useQuery` でデータを取得して表示できる
- [ ] loading / error が適切にハンドリングされている
- [ ] `any` を使っていない
- [ ] variables を使って特定の1件を取得できる

---

## 詰まったときの調べ方

| 詰まりポイント | 調べる場所 |
|-------------|-----------|
| ApolloProvider の設定 | [公式: Get started](https://www.apollographql.com/docs/react/get-started) |
| useQuery の使い方 | [公式: Queries](https://www.apollographql.com/docs/react/data/queries) |
| useQuery の型パラメータ | `useQuery<TData, TVariables>` |
| gql タグとは | Apollo 公式: gql |
| null の安全な扱い | TypeScript `??` 演算子 |
