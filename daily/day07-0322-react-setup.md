# 3/22（日）｜React環境構築・コンポーネント基礎

**所要目安**: 6〜8時間  
**参照リソース**: react.dev「Quick Start」「Your First Component」「Passing Props to a Component」  
**AIなしで取り組む。帰宅後にClaudeと振り返る。**

---

## 今日読む箇所（30分）

react.dev を開いて上記3節を読む。

読みながら意識すること：
- コンポーネントとは何か（関数との違い）
- JSX は何をしているのか
- props はどう渡して、どう受け取るか

---

## 課題

### Part 1：環境起動確認

```bash
docker compose up -d
docker compose exec frontend-dev sh
cd project3-dashboard
npm install
npm run dev -- --host
```

`localhost:5173` で画面が表示されることを確認する。

### Part 2：型定義を作る

`src/types/ad.ts` を新規作成する。

```
広告パフォーマンスデータ（AdPerformance）
  → 広告ID・広告名・インプレッション数・クリック数
  ※ フィールド名・型は自分で決める（string / number）
  ※ CTR はここでは持たない（表示時に計算する）
```

### Part 3：AdCard コンポーネントを作る

`src/components/AdCard.tsx` を新規作成する。

```
・props で AdPerformance を受け取る
・広告名・インプレッション数・クリック数・CTR（小数点2桁・%）を表示する
・インプレッションが 0 のときはゼロ除算しない
```

### Part 4：App.tsx で AdCard を並べる

ハードコードしたデータで AdCard を3件表示する。

### Part 5：考察

```typescript
// Q1: props に型を付ける理由は何か
// Q2: JSX は最終的に何に変換されるか
// Q3: コンポーネント名を大文字始まりにする理由は何か
```

---

## 完了条件

- [ ] `localhost:5173` で3件の AdCard が表示される
- [ ] CTR が正しく計算・表示される
- [ ] `any` を使っていない

---

## 詰まったときの調べ方

| 詰まりポイント | 調べるキーワード |
|-------------|----------------|
| コンポーネントの作り方 | react.dev「Your First Component」 |
| props の型定義 | `TypeScript React props type` |
| 小数点2桁の表示 | `JavaScript toFixed` |
