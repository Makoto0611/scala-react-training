# 課題3 - 入札API簡易実装

**目安時間**: 6〜8時間
**参照書籍**: 実践Scala入門 第3章「Either」・第5章「Future」

---

## 読む箇所（先に読んでから実装に入ること）

実践Scala入門 第3章「Either」全体・第5章「Futureの基本的な使い方」を読む。

- Either の Left / Right は何を表すか（慣習的な使い方）
- Option と Either の使い分け
- Future は何を解決するものか

---

## 仕様

`src/project2-bid-api/` に実装する。

### エンドポイント

`POST /bid`

**リクエスト**
```json
{"requestId": "req_001", "floorPrice": 100}
```

**レスポンス（入札あり）**
```json
{"requestId": "req_001", "adId": "ad_001", "bidPrice": 150, "won": true}
```

**レスポンス（入札なし）**
```json
{"requestId": "req_001", "adId": null, "bidPrice": 0, "won": false, "reason": "NO_BID"}
```

### 広告データ（ハードコードでよい）

```scala
// bidPrice はその広告が払える最大入札価格
val adCatalog = List(
  BidAd("ad_001", "テスト広告A", bidPrice = 150),
  BidAd("ad_002", "テスト広告B", bidPrice = 80),
  BidAd("ad_003", "テスト広告C", bidPrice = 200)
)
```

### ロジック

```
1. floorPrice 以上の bidPrice を持つ広告を探す
2. 条件を満たす広告が複数あれば bidPrice が最も高いものを選ぶ
3. 条件を満たす広告がなければ NO_BID を返す
4. 処理は Future で非同期にする
```

---

## 実装要件

1. Tapir でエンドポイントを定義する
2. Either[BidError, BidResponse] でエラーハンドリングする
3. 処理は Future で非同期にする
4. CORS を設定する（Reactからのリクエストを受け入れる）

---

## 採点基準

```bash
# 入札あり（floorPrice=100 → ad_003 が bidPrice=200 で勝つ）
curl -X POST http://localhost:8080/bid \
  -H "Content-Type: application/json" \
  -d '{"requestId":"req_001","floorPrice":100}'

# 入札なし（floorPrice が全広告の bidPrice を超える）
curl -X POST http://localhost:8080/bid \
  -H "Content-Type: application/json" \
  -d '{"requestId":"req_002","floorPrice":99999}'
```

- [ ] 入札ありケースで adId・bidPrice を含むレスポンスが返る
- [ ] 入札なしケースで `"won": false` と `"reason": "NO_BID"` が返る
- [ ] `Either` でエラーハンドリングしている
- [ ] `null` を使っていない
- [ ] CORS 設定がある（Reactから呼べる）

---

## 詰まったときの調べ方

| 詰まりポイント | 調べる場所 |
|-------------|-----------|
| Tapir の基本 | tapir.softwaremill.com / `tapir scala hello world` |
| Tapir の JSON 設定 | `tapir circe json` |
| CORS 設定 | `tapir cors` |
| Future + Either の組み合わせ | `Scala Future Either` |

<details>
<summary>ヒント：Tapir の依存関係</summary>

```scala
libraryDependencies ++= Seq(
  "com.softwaremill.sttp.tapir" %% "tapir-core"              % "1.9.0",
  "com.softwaremill.sttp.tapir" %% "tapir-http4s-server"     % "1.9.0",
  "com.softwaremill.sttp.tapir" %% "tapir-circe"             % "1.9.0",
  "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % "1.9.0",
  "org.http4s"                  %% "http4s-ember-server"      % "0.23.24",
  "io.circe"                    %% "circe-generic"            % "0.14.6"
)
```

</details>
