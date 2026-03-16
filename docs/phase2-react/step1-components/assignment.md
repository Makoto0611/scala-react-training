# Step1 - コンポーネント・props・型定義

**参照リソース**: react.dev「Quick Start」「Your First Component」「Passing Props to a Component」
**目安時間**: 4〜6時間

---

## 読む箇所

react.dev を開いて上記3節を読む。

読みながら意識すること：
- コンポーネントとは何か（なぜ関数で書くのか）
- JSX は最終的に何に変換されるか
- props はどこで定義してどこで受け取るか
- TypeScript でどう型を付けるか

---

## 課題

`src/project3-dashboard/src/` 配下に実装する。

### Part1：型定義を設計する

`src/types/content.ts` を新規作成する。

phase1の課題1で生成される `contents.json` のデータ構造に合わせて型を定義する。
フィールド名・型は自分で設計すること（`any` は使わない）。

```
コンテンツパフォーマンスデータ（ContentPerformance）
  → コンテンツID・視聴数・いいね数・いいね率
```

### Part2：ContentCard コンポーネントを作る

`src/components/ContentCard.tsx` を新規作成する。

要件：
```
- props で ContentPerformance を受け取る
- コンテンツID・視聴数・いいね数・いいね率（小数点2桁・%表示）を表示する
- 視聴数が 0 のときゼロ除算しない
- props の型を明示する（any を使わない）
```

### Part3：App.tsx でリスト表示する

ハードコードしたデータで ContentCard を5件表示する。

要件：
```
- 5件の ContentPerformance データをコード内で定義する
- map を使ってリスト表示する
- key を適切に設定する
```

### Part4：起動確認

`npm run dev` で起動し、`localhost:5173` でブラウザ表示できることを確認する。

---

## 完了条件

- [ ] `localhost:5173` で5件のContentCardが表示される
- [ ] いいね率が小数点2桁で表示される
- [ ] `any` を使っていない
- [ ] `key` が設定されている
- [ ] TypeScriptのコンパイルエラーがない

---

## 詰まったときの調べ方

| 詰まりポイント | 調べる場所 |
|-------------|-----------|
| コンポーネントの作り方 | react.dev「Your First Component」 |
| props の型定義 | `TypeScript React props interface` |
| map でリスト表示 | react.dev「Rendering Lists」 |
| toFixed の使い方 | `JavaScript toFixed` |

<details>
<summary>ヒント1：props の型定義パターン</summary>

```typescript
interface ContentCardProps {
  data: ContentPerformance;
}

const ContentCard = ({ data }: ContentCardProps) => {
  return <div>{data.contentId}</div>;
};
```

</details>

<details>
<summary>ヒント2：いいね率の計算とフォーマット</summary>

```typescript
const likeRate = views === 0 ? 0 : (likes / views) * 100;
const formatted = likeRate.toFixed(2); // "50.00"
```

</details>
