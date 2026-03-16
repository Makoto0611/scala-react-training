# Step6 - 応用構文・リファクタリング

**参照書籍**: 実践Scala入門 第8章・第9章
**目安時間**: 3〜5時間

---

## 読む箇所

実践Scala入門 第8章・第9章を以下の節を読む。

**第8章**
- コンパニオンオブジェクト
- 部分関数
- デフォルト引数・名前付き引数
- 値クラス
- 複数の引数リストを持つメソッド

**第9章**
- 可能な限り不変にする
- 式指向なスタイルで書く

---

## 課題

step1〜5で書いたコードをリファクタリングする。
動作は変えずに、より「Scalaらしい」コードに改善する。

### Part1：コンパニオンオブジェクトを使う

Content の生成にバリデーションを追加する。

```
コンパニオンオブジェクトに apply メソッドを定義する。
以下の条件を満たさない場合は Either[String, Content] の Left を返す。
  - contentId が空文字ではない
  - title が空文字ではない
  - duration が 0 以上である
```

これにより `Content("", "タイトル", Movie, 100)` のような無効な Content が直接作れなくなる。

### Part2：値クラスで型安全にする

`contentId: String` の代わりに `ContentId` という値クラスを定義し、
String と ContentId を混在させるミスをコンパイル時に防ぐ。

```scala
case class ContentId(value: String) extends AnyVal
```

Content / View / Like の contentId フィールドを全て ContentId 型に変更し、
既存のコードが引き続き動作することを確認する。

### Part3：部分関数でパターンマッチを整理する

ContentType ごとの表示ラベルを生成する処理を部分関数で実装し直す。

```scala
val contentTypeLabel: PartialFunction[ContentType, String] = {
  case Movie        => ???
  case Series(n)    => ???
  case Live         => ???
}
```

### Part4：式指向スタイルに統一する

step1〜5で書いたコードの中で `var` を使っている箇所があれば `val` に変える。
また、`return` を使っている箇所があれば式指向スタイルに書き直す。

---

## 完了条件

- [ ] Content のバリデーションが動作する（無効な引数で Left が返る）
- [ ] ContentId 値クラスが定義されていて全ての case class で使われている
- [ ] contentTypeLabel が部分関数で実装されている
- [ ] `var` を使っていない
- [ ] `return` を使っていない
- [ ] `sbt test` で全テストが通る（リファクタリング後も壊れていない）

---

## 詰まったときの調べ方

| 詰まりポイント | 調べる場所 |
|-------------|-----------|
| コンパニオンオブジェクト | 実践Scala入門 第8章 / レシピ083〜085 |
| 値クラス（AnyVal） | 実践Scala入門 第8章 |
| 部分関数 | 実践Scala入門 第8章 / レシピ058〜060 |
| apply メソッド | `Scala companion object apply` |

<details>
<summary>ヒント1：コンパニオンオブジェクトの基本</summary>

```scala
case class Content private (id: ContentId, title: String, contentType: ContentType, duration: Int)

object Content {
  def apply(id: ContentId, title: String, contentType: ContentType, duration: Int): Either[String, Content] = {
    if (id.value.isEmpty) Left("contentId が空です")
    else if (title.isEmpty) Left("title が空です")
    else if (duration < 0) Left("duration が負の値です")
    else Right(new Content(id, title, contentType, duration))
  }
}
```

</details>

<details>
<summary>ヒント2：値クラスの定義</summary>

```scala
case class ContentId(value: String) extends AnyVal
// 使い方
val id = ContentId("ct_001")
val str: String = id.value
```

</details>
