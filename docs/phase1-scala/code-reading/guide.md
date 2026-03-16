# コードリーディング - Scala

**目安時間**: 2〜3時間
**タイミング**: 課題1が完成した後に行う

---

## 目的

自分が書いたコードと「本物のScalaコード」を比較することで、
Scalaらしい書き方・設計パターンを体感で理解する。

---

## 読むリポジトリ

以下のいずれかを選んで読む（両方読んでもよい）。

### 選択肢A：circe（JSONライブラリ）
https://github.com/circe/circe

課題1で使った circe 自体のソースコードを読む。
自分が `decode` を呼んだ裏側でどんな処理が行われているかを追う。

**読む場所**
- `modules/core/src/main/scala/io/circe/` 配下
- `Decoder.scala` の冒頭部分

### 選択肢B：cats（関数型プログラミングライブラリ）
https://github.com/typelevel/cats

Option / Either の拡張機能がどう実装されているかを読む。

**読む場所**
- `core/src/main/scala/cats/instances/option.scala`
- `core/src/main/scala/cats/instances/either.scala`

---

## 課題

読み終わったら、以下を `code-reading/notes.md` にまとめる。

```
1. 自分のコードと比較して気づいた違いを3つ以上書く
2. 真似したいと思った書き方を1つ以上書く
3. まだ理解できなかった部分を正直に書く（わからなくて当然）
```

---

## 完了条件

- [ ] いずれかのリポジトリのコードを実際に読んだ
- [ ] notes.md に気づきが3つ以上書かれている
- [ ] まだ理解できなかった部分も正直に書かれている
