# Step1 - React → Scala API 連携

**目安時間**: 3〜4時間
**前提**: 課題3（Scala API）が `localhost:8080` で起動していること

---

## 課題

課題2のダッシュボードの「入札する」ボタンを、実際に Scala API に繋げる。

### Part1：CORS の確認

Scala API 側の CORS 設定が正しいか curl で確認する。

```bash
curl -X OPTIONS http://localhost:8080/bid \
  -H "Origin: http://localhost:5173" \
  -H "Access-Control-Request-Method: POST" \
  -v
```

`Access-Control-Allow-Origin` ヘッダーが返ってくることを確認する。

### Part2：入札リクエストを送信する

課題2の `AdTableRow.tsx` の「入札する」ボタンに実装を追加する。

```typescript
// 入札リクエストの型を定義する
interface BidRequest {
  requestId: string;
  floorPrice: number;
}

interface BidResponse {
  requestId: string;
  adId: string | null;
  bidPrice: number;
  won: boolean;
  reason?: string;
}
```

ボタンをクリックしたとき：
```
1. Scala API の POST /bid にリクエストを送る
2. floorPrice は各広告の ctr * 10 を使う（仮の計算でよい）
3. 結果をモーダルまたはアラートで表示する
   - won: true  → 「入札成功！ bidPrice: {価格}円」
   - won: false → 「入札なし（NO_BID）」
4. ローディング中はボタンを disabled にする
```

### Part3：エラーハンドリング

API が起動していない場合や、リクエストが失敗した場合のエラー表示を実装する。

```
- ネットワークエラー → 「APIに接続できませんでした」
- HTTP エラー → 「サーバーエラーが発生しました（ステータスコード）」
```

---

## 完了条件

- [ ] 「入札する」ボタンで Scala API にリクエストが飛ぶ
- [ ] 入札成功・入札なし・エラーが画面に表示される
- [ ] ローディング中はボタンが disabled になる
- [ ] `any` を使っていない
