# Step1 - React → Scala API 連携

**目安時間**: 3〜4時間
**前提**: 課題3（Scala API）が `localhost:8080` で起動していること

---

## 課題

課題2のダッシュボードの「レコメンドする」ボタンを、実際に Scala API に繋げる。

### Part1：CORS の確認

Scala API 側の CORS 設定が正しいか curl で確認する。

```bash
curl -X OPTIONS http://localhost:8080/recommend \
  -H "Origin: http://localhost:5173" \
  -H "Access-Control-Request-Method: POST" \
  -v
```

`Access-Control-Allow-Origin` ヘッダーが返ってくることを確認する。

### Part2：レコメンドリクエストを送信する

課題2の `ContentTableRow.tsx` の「レコメンドする」ボタンに実装を追加する。

```typescript
interface RecommendRequest {
  requestId: string;
  minScore: number;
}

interface RecommendResponse {
  requestId: string;
  contentId: string | null;
  score: number;
  title?: string;
  recommended: boolean;
  reason?: string;
}
```

ボタンをクリックしたとき：
```
1. Scala API の POST /recommend にリクエストを送る
2. minScore は各コンテンツの likeRate を整数に変換した値を使う
3. 結果をモーダルまたはアラートで表示する
   - recommended: true  → 「レコメンド成功！ score: {スコア} / {タイトル}」
   - recommended: false → 「レコメンドなし（NO_CONTENT）」
4. ローディング中はボタンを disabled にする
```

### Part3：エラーハンドリング

APIが起動していない場合や、リクエストが失敗した場合のエラー表示を実装する。

```
- ネットワークエラー → 「APIに接続できませんでした」
- HTTP エラー → 「サーバーエラーが発生しました（ステータスコード）」
```

---

## 完了条件

- [ ] 「レコメンドする」ボタンで Scala API にリクエストが飛ぶ
- [ ] レコメンド成功・なし・エラーが画面に表示される
- [ ] ローディング中はボタンが disabled になる
- [ ] `any` を使っていない
