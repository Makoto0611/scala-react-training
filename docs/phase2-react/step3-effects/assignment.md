# Step3 - useEffect・fetch・TypeScript型定義

**参照リソース**: react.dev「Synchronizing with Effects」/ TypeScript公式「Everyday Types」「Generics」
**目安時間**: 4〜6時間

---

## 読む箇所

- react.dev「Synchronizing with Effects」全体
- TypeScript公式「Everyday Types」「Generics」

読みながら意識すること：
- useEffect の依存配列が何を意味するか
- クリーンアップ関数はいつ必要か
- TypeScript のジェネリクス `<T>` をどう使うか
- `interface` と `type` の使い分け

---

## 課題

ハードコードしていたデータを、APIから取得する形に変える。

### Part1：useEffect でデータ取得する

モックJSONを `fetch` で取得する形に変更する。

`public/mock/contents.json` を作成し（step1のハードコードデータをJSONファイルに移す）、
useEffect で取得して表示する。

3つの状態を適切に管理すること：
```
- ローディング中 → 「読み込み中...」を表示する
- 取得成功 → リストを表示する
- 取得失敗 → 「データの取得に失敗しました」を表示する
```

### Part2：型定義を整備する

`src/types/content.ts` を更新し、以下を定義する。

```
1. ContentPerformance（step1のものを見直す）
2. ApiResponse<T>（data: T | null / loading: boolean / error: string | null）
3. SortKey（"views" | "likes" | "likeRate" のユニオン型）
```

ApiResponse<T> を使って useEffect の状態管理を書き直す。

### Part3：全コンポーネントの型を固める

全コンポーネントのpropsに型を付ける。

チェックリスト：
```
- [ ] App.tsx のローカル変数に型がある
- [ ] FilterInput.tsx の props に型がある
- [ ] ContentCard.tsx の props に型がある（step1で作成済みのはず）
- [ ] ソートボタン周りのイベントハンドラに型がある
- [ ] any を1箇所も使っていない
```

### Part4：fetch のエラーハンドリングを強化する

`fetch` は HTTP エラー（404・500等）で例外を投げない。
`response.ok` を使って HTTP エラーも適切にハンドリングする実装に変える。

---

## 完了条件

- [ ] useEffect でモックJSONからデータが取得できる
- [ ] ローディング・エラー・成功の3状態が表示される
- [ ] ApiResponse<T> が定義されて使われている
- [ ] `response.ok` でHTTPエラーをハンドリングしている
- [ ] `any` を使っていない
- [ ] TypeScriptのコンパイルエラーがない

---

## 詰まったときの調べ方

| 詰まりポイント | 調べる場所 |
|-------------|-----------|
| useEffect の依存配列 | react.dev「Synchronizing with Effects」 |
| fetch の書き方 | `TypeScript fetch async await` |
| response.ok の使い方 | `fetch response.ok` |
| ジェネリクスの基本 | TypeScript公式「Generics」 |
| interface vs type | `TypeScript interface vs type` |

<details>
<summary>ヒント1：fetch の基本パターン</summary>

```typescript
useEffect(() => {
  const fetchData = async () => {
    try {
      const res = await fetch("/mock/contents.json");
      if (!res.ok) throw new Error(`HTTP error: ${res.status}`);
      const data: ContentPerformance[] = await res.json();
      // state を更新
    } catch (e) {
      // エラー state を更新
    }
  };
  fetchData();
}, []);
```

</details>

<details>
<summary>ヒント2：ジェネリクスの活用例</summary>

```typescript
interface ApiResponse<T> {
  data: T | null;
  loading: boolean;
  error: string | null;
}

// 使い方
const [state, setState] = useState<ApiResponse<ContentPerformance[]>>({
  data: null,
  loading: true,
  error: null,
});
```

</details>
