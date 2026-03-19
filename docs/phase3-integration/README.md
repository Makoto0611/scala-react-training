# Phase3 - 統合・デモ完成

## このphaseのゴール

**3つの成果物が繋がって動き、3/30にデモ発表できること。**

```
課題1（Scala CLI）→ contents.json 生成
課題3（Scala API）→ POST /recommend でレコメンド処理
課題2（React）    → contents.json 表示 + Scala APIにレコメンドリクエストを送信
```

---

## step一覧

| step | テーマ | 目安時間 |
|------|--------|---------|
| 課題3 | Scala APIの実装（Tapir + Either） | 6〜8時間 |
| step1 | CORS設定・React→Scala API連携 | 3〜4時間 |
| step2 | デモシナリオ作成・動作確認 | 2〜3時間 |

---

## 完了条件（phase3全体）

- [ ] 課題3の `sbt run` で API が起動する
- [ ] `POST /recommend` に curl でリクエストが通る
- [ ] Reactの「レコメンドする」ボタンでScala APIに繋がる
- [ ] 3つが繋がった状態でデモできる
