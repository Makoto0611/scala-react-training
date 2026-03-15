# 3/15（日）｜Step1：環境構築

**目標**: `docker compose up` でScala開発環境が起動し、REPLで動作確認できる

---

## やること

### 1. GitHubリポジトリ作成

1. GitHub（https://github.com）にアクセス
2. 「New repository」をクリック
3. 以下の設定で作成する
   - Repository name: `scala-react-practice`
   - Public
   - README: チェックしない（すでにあるため）

### 2. ローカルからpush

ターミナルで以下を実行する（ディレクトリはすでに存在する）

```bash
cd ..\scala-react-practice
git init
git add .
git commit -m "initial commit: プロジェクト構成・Docker設定追加"
git branch -M main
git remote add origin https://github.com/<あなたのユーザー名>/scala-react-practice.git
git push -u origin main
```

### 3. Dockerで環境起動

```bash
docker compose up -d
docker compose ps
```

`scala-dev` が `running` になっていればOK

### 4. コンテナに入ってREPL起動

```bash
docker compose exec scala-dev bash
cd project1-log-aggregator
sbt console
```

### 5. REPLで動作確認

```scala
val x = 10
var y = 20
y = 30
def add(a: Int, b: Int): Int = a + b
add(1, 2)
List(1, 2, 3).map(_ * 2)
List(1, 2, 3, 4, 5).filter(_ % 2 == 0)
```

### 6. sbt run を試す

```bash
:quit
sbt run
```

`Hello, Scala!` と表示されればOK

---

## 完了条件

- [ ] GitHubにpushできた
- [ ] `scala-dev` が running になっている
- [ ] REPLが起動した
- [ ] `List(1, 2, 3).map(_ * 2)` が `List(2, 4, 6)` を返した
- [ ] `sbt run` で `Hello, Scala!` が表示された

---

## 詰まりやすいポイント

- Dockerが起動しない → Docker Desktopが起動しているか確認
- sbt console が進まない → 初回は5〜10分かかる
- git pushエラー → ユーザー名・リポジトリ名を確認
