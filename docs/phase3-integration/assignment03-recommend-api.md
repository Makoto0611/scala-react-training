# 課題3 - レコメンドAPI簡易実装

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

`src/project2-recommend-api/` に実装する。

### エンドポイント

`POST /recommend`

**リクエスト**
```json
{"requestId": "req_001", "minScore": 100}
```

**レスポンス（レコメンドあり）**
```json
{"requestId": "req_001", "contentId": "content_001", "score": 150, "recommended": true}
```

**レスポンス（レコメンドなし）**
```json
{"requestId": "req_001", "contentId": null, "score": 0, "recommended": false, "reason": "NO_MATCH"}
```

### コンテンツデータ（ハードコードでよい）

```scala
// score はそのコンテンツのレコメンドスコア
val contentCatalog = List(
  RecommendContent("content_001", "猫の日常",          score = 150),
  RecommendContent("content_002", "料理チュートリアル", score = 80),
  RecommendContent("content_003", "ゲーム実況",         score = 200)
)
```

### ロジック

```
1. minScore 以上の score を持つコンテンツを探す
2. 条件を満たすコンテンツが複数あれば score が最も高いものを選ぶ
3. 条件を満たすコンテンツがなければ NO_MATCH を返す
4. 処理は Future で非同期にする
```

---

## 実装要件

1. Tapir でエンドポイントを定義する
2. Either[RecommendError, RecommendResponse] でエラーハンドリングする
3. 処理は Future で非同期にする
4. CORS を設定する（Reactからのリクエストを受け入れる）

---

## 採点基準

```bash
# レコメンドあり（minScore=100 → content_003 が score=200 で選ばれる）
curl -X POST http://localhost:8080/recommend \
  -H "Content-Type: application/json" \
  -d '{"requestId":"req_001","minScore":100}'

# レコメンドなし（minScore が全コンテンツの score を超える）
curl -X POST http://localhost:8080/recommend \
  -H "Content-Type: application/json" \
  -d '{"requestId":"req_002","minScore":99999}'
```

- [ ] レコメンドありケースで contentId・score を含むレスポンスが返る
- [ ] レコメンドなしケースで `"recommended": false` と `"reason": "NO_MATCH"` が返る
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
