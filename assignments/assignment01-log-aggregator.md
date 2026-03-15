# 独立課題①：広告ログ集計CLIツール（3/21）

**所要目安**: 6〜8時間（LPIC模擬試験②と並行）  
**参照書籍**: 実践Scala入門 第3章・第4章  
**午前：LPIC模擬試験② / 午後：課題実装**

---

## LPIC模擬試験②（午前中）

- Ping-t で模擬試験を1回実施する
- 目標：60%以上
- 結果を記録してClaudeに共有する

---

## 仕様

`project1-log-aggregator` に実装する。

**入力**（`src/main/resources/` に配置済み）：

`impressions.json`
```json
[
  {"adId": "ad_001", "timestamp": 1700000000},
  {"adId": "ad_001", "timestamp": 1700000010},
  {"adId": "ad_002", "timestamp": 1700000020},
  {"adId": "ad_003", "timestamp": 1700000030}
]
```

`clicks.json`
```json
[
  {"adId": "ad_001", "timestamp": 1700000005},
  {"adId": "ad_002", "timestamp": 1700000025}
]
```

**期待する出力**：
```
=== 広告パフォーマンスレポート ===
ad_001: impressions=2, clicks=1, CTR=50.00%
ad_002: impressions=1, clicks=1, CTR=100.00%
ad_003: impressions=1, clicks=0, CTR=0.00%
```

---

## 実装要件

1. `circe` ライブラリでJSONを読み込む（`build.sbt` は設定済み）
2. `case class` でデータモデルを定義する（今週のものを流用してよい）
3. `adId` ごとにインプレッション・クリック数を集計する
4. CTR（クリック率 = clicks / impressions × 100）を計算する
5. ゼロ除算が発生しないようにする
6. 結果を整形して出力する

---

## 採点基準

- [ ] `sbt run` で動作する
- [ ] 期待する出力と一致する
- [ ] `for` ループを使っていない
- [ ] ゼロ除算が起きない
- [ ] `null` を使っていない（`Option` を使う）

---

## circe の使い方ヒント

```
1. ファイルを文字列として読み込む
2. circe でパースして List[CaseClass] に変換する
3. 失敗した場合のエラー処理をする
```

調べるキーワード：`circe decode Scala` / `circe generic auto`

---

## 詰まったときの順番

1. 実践Scala入門 第3・4章を確認する
2. 逆引きレシピ（レシピ050 / 113 / 118 / 120 / 123）を確認する
3. キーワード検索する
4. 帰宅後にClaudeに質問する

---

## 振り返りメモ欄（帰宅後に記入）

```
・詰まったポイント：

・解決した方法：

・次に活かすこと：
```
