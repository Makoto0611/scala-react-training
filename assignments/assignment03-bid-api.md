# 独立課題③：入札API簡易実装（3/29）

**所要目安**: 6〜8時間  
**参照書籍**: 実践Scala入門 第3章「Either」・第5章「Future」  
**AIなしで午前中取り組む。午後はClaudeと並走する。**

---

## 今日読む箇所（1時間）

実践Scala入門 第3章「Either」全体・第5章「Futureの基本的な使い方」を読む。

読みながら意識すること：
- `Either[L, R]` の `Left` / `Right` は何を表すか（慣習的な使い方）
- `Option` と `Either` はどう使い分けるか
- `Future` は何を解決するものか

---

## 仕様

`project2-bid-api` に実装する。

**エンドポイント**: `POST /bid`

リクエスト：
```json
{"requestId": "req_001", "userId": "user_123", "floorPrice": 100}
```

レスポンス（入札あり）：
```json
{"requestId": "req_001", "adId": "ad_001", "bidPrice": 150, "adName": "テスト広告A"}
```

レスポンス（入札なし）：
```json
{"requestId": "req_001", "adId": null, "reason": "NO_BID"}
```

---

## 実装要件

1. Tapir でエンドポイントを定義する
2. 広告リスト（ハードコード・5件程度）を用意する
3. `floorPrice` 以上の `bidPrice` を持つ広告を探す
4. 条件に合う広告があれば入札レスポンスを返す
5. 条件に合わなければ NO_BID を返す
6. `Either[BidError, BidResponse]` でエラーハンドリングする

---

## 採点基準

```bash
# 入札あり
curl -X POST http://localhost:8080/bid \
  -H "Content-Type: application/json" \
  -d '{"requestId":"req_001","userId":"user_123","floorPrice":100}'

# 入札なし
curl -X POST http://localhost:8080/bid \
  -H "Content-Type: application/json" \
  -d '{"requestId":"req_002","userId":"user_123","floorPrice":99999}'
```

- [ ] 入札ありケースで adId を含むレスポンスが返る
- [ ] 入札なしケースで `"reason": "NO_BID"` が返る
- [ ] `Either` でエラーハンドリングしている
- [ ] `null` を使っていない

---

## Tapir の調べ方

`tapir scala hello world` / `tapir endpoint POST JSON` / tapir公式（tapir.softwaremill.com）

---

## 振り返りメモ欄（夜に記入）

```
・詰まったポイント：

・Either を使ってよかった点：

・Option と Either の使い分けで気づいたこと：

・4月以降に繋げたいこと：
```
