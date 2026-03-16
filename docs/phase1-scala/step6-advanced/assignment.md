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

Ad の生成に バリデーションを追加する。

```
コンパニオンオブジェクトに apply メソッドを定義する。
以下の条件を満たさない場合は Either[String, Ad] の Left を返す。
  - adId が空文字ではない
  - name が空文字ではない
  - budget が 0 以上である
```

これにより `Ad("", "テスト", Banner, 100)` のような無効な Ad が直接作れなくなる。

### Part2：値クラスで型安全にする

`adId: String` の代わりに `AdId` という値クラスを定義し、
String と AdId を混在させるミスをコンパイル時に防ぐ。

```scala
case class AdId(value: String) extends AnyVal
```

Ad / Impression / Click の adId フィールドを全て AdId 型に変更し、
既存のコードが引き続き動作することを確認する。

### Part3：部分関数でパターンマッチを整理する

AdType ごとの表示ラベルを生成する処理を部分関数で実装し直す。

```scala
val adTypeLabel: PartialFunction[AdType, String] = {
  case Banner     => ???
  case Video(sec) => ???
  case Native     => ???
}
```

### Part4：式指向スタイルに統一する

step1〜5で書いたコードの中で `var` を使っている箇所があれば `val` に変える。
また、`return` を使っている箇所があれば式指向スタイルに書き直す。

---

## 完了条件

- [ ] Ad のバリデーションが動作する（無効な引数で Left が返る）
- [ ] AdId 値クラスが定義されていて全ての case class で使われている
- [ ] adTypeLabel が部分関数で実装されている
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
case class Ad private (id: AdId, name: String, adType: AdType, budget: Int)

object Ad {
  def apply(id: AdId, name: String, adType: AdType, budget: Int): Either[String, Ad] = {
    if (id.value.isEmpty) Left("adId が空です")
    else if (name.isEmpty) Left("name が空です")
    else if (budget < 0) Left("budget が負の値です")
    else Right(new Ad(id, name, adType, budget))
  }
}
```

</details>

<details>
<summary>ヒント2：値クラスの定義</summary>

```scala
case class AdId(value: String) extends AnyVal
// 使い方
val id = AdId("ad_001")
val str: String = id.value
```

</details>
