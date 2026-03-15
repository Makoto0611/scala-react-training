# 日付別詳細カリキュラム（scala-react-practice）

**対象リポジトリ**: scala-react-practice  
**期間**: 2026年3月15日〜3月30日  
**Scalaバージョン**: 3系  
**学習スタイル**: 研修中に書籍で調べながら自力で悩む → 帰宅後にClaudeと解決

---

## 各dayファイルの構成

```
【今日読む箇所】実践Scala入門 or React公式の該当節（30分）
【課題】        仕様だけ渡す → 自力で実装
【詰まったとき】書籍レシピ番号 or 調べるキーワード
【帰宅後】      コードをClaudeに見せてフィードバック
```

---

## 🗓 Week 1：Scala基礎〜独立課題①

| 日付 | ファイル | 内容 | 参照書籍 |
|------|--------|------|---------|
| 3/15 | `daily/day01-0315-setup.md` | 環境構築・REPL起動 | - |
| 3/16 | `daily/day02-0316-case-class.md` | case classでデータモデル設計 | 実践Scala入門 第2章 |
| 3/17 | `daily/day03-0317-map-filter.md` | map / filter | 実践Scala入門 第4章 |
| 3/18 | `daily/day04-0318-fold-groupby.md` | fold / groupBy | 実践Scala入門 第4章 |
| 3/19 | `daily/day05-0319-pattern-match.md` | パターンマッチ・sealed trait | 実践Scala入門 第2章 |
| 3/20 | `daily/day06-0320-option-design.md` | Option・課題①設計 | 実践Scala入門 第3章 |
| 3/21 | `assignments/assignment01-log-aggregator.md` | **独立課題①** | 実践Scala入門 第3・4章 |

---

## 🗓 Week 2：React/TS基礎〜独立課題②③

| 日付 | ファイル | 内容 | 参照リソース |
|------|--------|------|------------|
| 3/22 | `daily/day07-0322-react-setup.md` | コンポーネント・props・型定義 | react.dev |
| 3/23〜24 | `daily/day08-09-0323-0324-usestate.md` | useState・イベント・条件レンダリング | react.dev |
| 3/25〜26 | `daily/day10-11-0325-0326-useeffect-types.md` | useEffect・fetch・TypeScript型定義 | react.dev・TS公式 |
| 3/27 | `daily/day12-0327-design.md` | 課題②設計・モックデータ作成 | - |
| 3/28 | `assignments/assignment02-dashboard.md` | **独立課題②** | react.dev |
| 3/29 | `assignments/assignment03-bid-api.md` | **独立課題③** | 実践Scala入門 第3・5章 |
| 3/30 | バッファ・振り返り | 未完了課題の仕上げ・README更新 | - |

---

## 📌 毎日のLPIC（1時間）

```
Plan → Do（Ping-t 45分）→ Check → Act
```

**模擬試験**:
- 3/21（土）: 模擬試験② → 目標60%以上
- 3/28（土）: 模擬試験③ → 目標80%以上 → 達成次第月末受験

---

## 🔗 4月以降（フェーズ3）

```
課題③ Scala入札API（port:8080）← POST /bid
課題② React ダッシュボードから入札リクエストを送信
課題① Scala集計CLIで生成した ads.json を共用
```

---

## 📚 使用書籍・リソース

詳細は `books/README.md` を参照。

| リソース | 使用フェーズ |
|---------|-----------|
| 実践Scala入門 | Week 1 全日・3/29 |
| Scala逆引きレシピ | 詰まったとき随時 |
| 関数プログラミング実践入門 | 概念補強（随時） |
| react.dev（公式） | Week 2 前半 |
| TypeScript公式 | Week 2 中盤 |
