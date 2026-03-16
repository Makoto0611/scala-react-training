# Step2 - useState・イベント・条件レンダリング

**参照リソース**: react.dev「State: A Component's Memory」「Responding to Events」「Conditional Rendering」「Rendering Lists」
**目安時間**: 4〜6時間

---

## 読む箇所

上記4節を読む。

読みながら意識すること：
- なぜ state が必要なのか（普通の変数との違い）
- useState の型はどう付けるか
- レンダリングとはいつ起きるか
- 条件付きレンダリングのパターン（三項・&&・if）

---

## 課題

step1で作ったコンポーネントに、インタラクティブな機能を追加する。

### Part1：フィルター機能を実装する

`src/components/FilterInput.tsx` を新規作成する。

要件：
```
- テキストボックスに文字を入力すると、広告IDに含まれる広告だけ表示される
- テキストボックスが空のときは全件表示
- 「クリア」ボタンでテキストボックスを空にして全件表示に戻る
- フィルター状態（入力文字列）を useState で管理する
```

どのコンポーネントが state を持つべきか、実装前に自分で設計すること。

### Part2：0件のときの表示

フィルター結果が0件のとき「該当する広告がありません」を表示する。
条件付きレンダリングを3通りの書き方（三項・&&・if）で実装し、
自分が読みやすいと思う書き方に統一する。

### Part3：選択状態を管理する

各 AdCard をクリックすると選択状態になり、背景色が変わる。

要件：
```
- 別の AdCard をクリックすると選択が移る
- 同じ AdCard を再クリックすると選択が解除される
- 選択中の adId を useState で管理する（selectedAdId: string | null）
```

### Part4：ソート機能を追加する

「インプレッション数」「クリック数」「CTR」のいずれかでソートできるようにする。

要件：
```
- ソートキーを選ぶボタンを3つ用意する
- 選択したキーで降順ソートする
- 現在のソートキーをハイライト表示する
```

---

## 完了条件

- [ ] フィルター機能が動作する
- [ ] クリアボタンが動作する
- [ ] 0件表示が動作する
- [ ] 選択状態（ハイライト）が動作する
- [ ] ソート機能が動作する
- [ ] `any` を使っていない
- [ ] `key` が設定されている

---

## 詰まったときの調べ方

| 詰まりポイント | 調べる場所 |
|-------------|-----------|
| useState の基本 | react.dev「State: A Component's Memory」 |
| onChange イベント | `TypeScript React onChange event type` |
| 条件付きレンダリング | react.dev「Conditional Rendering」 |
| 文字列の部分一致 | `JavaScript String includes` |
| 配列のソート（コピーしてからソート） | `JavaScript Array sort slice` |

<details>
<summary>ヒント1：state をどこで持つか</summary>

フィルター文字列と選択状態は、AdCard の親コンポーネントで持つ必要がある。
子から親への通知は props でコールバック関数を渡す。

</details>

<details>
<summary>ヒント2：ソートで元の配列を壊さない</summary>

```typescript
const sorted = [...data].sort((a, b) => b.impressions - a.impressions);
```

`sort` は元の配列を変更するので、スプレッド構文でコピーしてからソートする。

</details>
