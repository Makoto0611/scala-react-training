# 3/25（水）～3/26（木）｜useEffect・TypeScript型定義

**所要目安**: 各日 6〜8時間  
**参照リソース**: react.dev「Synchronizing with Effects」/ TypeScript公式「Everyday Types」「Generics」

---

## 課題（3/25）

### Part 1：ハードコードからAPIに切り替える

モックAPI `https://jsonplaceholder.typicode.com/posts` からデータを取得する形に変える。

```
・useEffect でマウント時にデータを取得する
・取得中は「読み込み中...」を表示する
・取得後は一覧を表示する
・エラー時は「データの取得に失敗しました」を表示する
```

状態として何が必要かを先に設計する。

### Part 2：型定義を書く

取得データの型を `src/types/post.ts` に定義する。`any` を使わない。

### Part 3：考察

```typescript
// Q1: useEffect の第2引数（依存配列）が [] のとき何を意味するか？
// Q2: ローディング・エラー・データの3状態を別々のuseStateで持つ場合と
//     1つにまとめる場合の違いは何か
// Q3: fetch でエラーが起きたとき catch だけでは不十分なケースがある。なぜか？
```

---

## 課題（3/26）

### Part 4：型定義を整備する

`src/types/ad.ts` を更新して以下を定義する。

```
1. AdPerformance（3/22で定義済みのものを見直す）
2. ApiResponse<T>（data: T / loading: boolean / error: string | null）
3. FilterState（何を持つべきか自分で設計する）
```

### Part 5：全コンポーネントを型で固める

全コンポーネントのpropsに型を付ける。`any` を1箇所も使わない。

### Part 6：考察

```typescript
// Q1: interface と type の使い分け基準を説明せよ
// Q2: ジェネリクス <T> を使う理由を ApiResponse を例に説明せよ
// Q3: string | null と string | undefined の違いは何か
```

---

## 完了条件（3/25）
- [ ] useEffect でデータが取得できる
- [ ] 3状態が表示される
- [ ] 型定義がある（any なし）

## 完了条件（3/26）
- [ ] ApiResponse<T> が定義されている
- [ ] 全コンポーネントのpropsに型がある
- [ ] any を使っていない
