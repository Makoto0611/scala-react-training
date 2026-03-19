# 課題4 - Phase2ダッシュボードを Apollo に書き換える

**目安時間**: 1.5〜2時間

---

## 課題の概要

Phase2で作成した広告パフォーマンスダッシュボードを、
`useEffect + fetch` による REST API 取得から
Apollo Client（`useQuery`）による GraphQL 取得に書き換える。

Step1〜3 で学んだ内容をすべて使う統合課題。

---

## ステップ

### Step1：GraphQL Schema を設計する

`src/graphql/schema.graphql` を新規作成し、
Phase2 の `ContentPerformance` 型に対応する GraphQL Schema を定義する。

Step1 Part3 で設計したものをベースにしてよい。

最低限以下を含めること：
- `ContentPerformance` 型の定義
- `contentPerformances` を返す Query の定義
- 特定の1件を返す Query の定義（id 引数あり）

### Step2：モックサーバーを更新する

Step3 で作成した `mock-server.mjs` に、コンテンツパフォーマンスのデータとリゾルバを追加する。

```
追加するデータ例（中身は自分で設計する）:
- ct_001: views=120, likes=60
- ct_002: views=80, likes=80
- ct_003: views=50, likes=0
```

Step3 の `campaigns` のリゾルバを参考にして実装する。

### Step3：`useEffect + fetch` を `useQuery` に書き換える

Phase2 のデータ取得部分を書き換える。

```
変更方針:
- useEffect・useState（データ取得用）・fetch を削除する
- gql でクエリを定義する（src/graphql/queries.ts に管理する）
- useQuery でデータを取得する
- loading / error のハンドリングを Apollo に委譲する
- `any` を使わず、GraphQL のレスポンスに合わせた型を定義する
```

<details>
<summary>ヒント：変更前後のイメージ</summary>

```typescript
// 変更前
const [data, setData] = useState<ContentPerformance[]>([])
const [loading, setLoading] = useState(true)

useEffect(() => {
  fetch('/api/contents')
    .then(res => res.json())
    .then(json => {
      setData(json)
      setLoading(false)
    })
}, [])

// 変更後
const { loading, error, data } = useQuery<GetContentsData>(GET_CONTENTS)
```

</details>

### Step4：動作確認

以下をすべて確認すること。

```
- localhost:5173 でダッシュボードが表示される
- Phase2 と同じデータが表示されている
- ブラウザの開発者ツール「ネットワーク」タブを開き、
  GraphQL リクエスト（POST /graphql）が発行されていることを確認する
- モックサーバーを止めるとエラーメッセージが表示されることを確認する
- モックサーバーを再起動すると正常に表示されることを確認する
```

---

## 完了条件

- [ ] `useEffect` / `fetch` / データ取得用の `useState` が削除されている
- [ ] `useQuery` でコンテンツパフォーマンスデータを取得している
- [ ] loading 中は「読み込み中...」が表示される
- [ ] error 時はエラーメッセージが表示される
- [ ] `any` を使っていない
- [ ] Phase2 と同じ表示・動作になっている
- [ ] ネットワークタブで GraphQL リクエストが確認できる

---

## 詰まったときの調べ方

| 詰まりポイント | 調べる場所 |
|-------------|-----------|
| useQuery の型定義 | Step2 の CountryList.tsx を参考にする |
| gql の書き方 | Step1・Step2 を参照 |
| ApolloProvider が設定されているか | `main.tsx` を確認する |
| リゾルバの書き方 | Step3 の mock-server.mjs を参照 |
