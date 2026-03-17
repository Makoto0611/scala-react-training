# Git運用ルール

## ブランチ構成

```
main
  └── workspace/makoto              ← 自分の学習進捗を積み上げるブランチ（developに相当）
        └── workspace/makoto-progress-step1   ← Step単位の作業ブランチ（featureに相当）
        └── workspace/makoto-progress-step2
        ...
```

### 各ブランチの役割

| ブランチ | 役割 | 直接コミット |
|---|---|---|
| `main` | 教材マスタ。誰も直接コミットしない | ❌ 禁止 |
| `workspace/{name}` | 自分の学習進捗の集積場所 | ❌ 基本禁止（マージのみ） |
| `workspace/{name}-progress-stepN` | Step単位の作業ブランチ | ✅ ここで実装する |

### ブランチ命名規則

| ブランチ種別 | 命名パターン | 例 |
|---|---|---|
| 学習進捗ベース | `workspace/{name}` | `workspace/makoto` |
| Step作業 | `workspace/{name}-progress-stepN` | `workspace/makoto-progress-step1` |

複数人が使う場合も `workspace/` 配下にまとまるため、GitHubのUIで人ごとに分類できる。

---

## 通常の運用フロー

### Step開始時
```bash
# workspace/{name} から progress ブランチを切る
git checkout workspace/{name}
git checkout -b workspace/{name}-progress-step2
```

### Step作業中
```bash
# 実装したらコミット（自分の実装ファイルのみ add する）
git add src/
git commit -m "feat: Step2 コレクション操作の実装"
```

### Step完了時
```bash
# workspace/{name} にマージ
git checkout workspace/{name}
git merge workspace/{name}-progress-step2

# 作業ブランチを削除
git branch -d workspace/{name}-progress-step2

# リモートに push
git push origin workspace/{name}
```

---

## 教材（main）が更新されたとき

```bash
# まず main を最新にする
git checkout main
git pull origin main

# workspace/{name} に戻って rebase
git checkout workspace/{name}
git rebase main
```

> **注意**: rebase後に `git push --force-with-lease origin workspace/{name}` が必要になる場合があります。

---

## 初期セットアップ（このリポジトリを使い始めるとき）

```bash
# リポジトリをクローン
git clone https://github.com/Makoto0611/scala-react-training.git
cd scala-react-training

# 自分のワークスペースブランチを作成
git checkout -b workspace/{自分の名前}
git push -u origin workspace/{自分の名前}
```

---

## コミットメッセージ規則

```
feat: Step1 データモデル設計と実装
feat: Step2 コレクション操作の実装
docs: Step1 復習メモ追加
fix: ContentType のパターンマッチ修正
```

| プレフィックス | 用途 |
|---|---|
| `feat` | 実装の追加・完了 |
| `docs` | ドキュメント追加・更新 |
| `fix` | バグ修正 |
| `refactor` | リファクタリング |
