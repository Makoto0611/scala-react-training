# Step4 - テスト

**参照書籍**: 実践Scala入門 第7章
**目安時間**: 3〜5時間

---

## 読む箇所

実践Scala入門 第7章を以下の節を読む。

- テストの重要性
- ScalaTestを使ったはじめてのテスト
- ScalaTestを使いこなす
- Mockitoを使ったモックテスト

---

## 課題

step1〜3で実装した処理に対してテストを書く。

テストファイルは `src/project1-log-aggregator/src/test/scala/` 配下に作成する。

### Part1：データモデルのテスト

`ContentModelsSpec.scala` を作成し、以下をテストする。

```
1. Content の case class が正しく生成できること
2. View の copy が正しく動作すること（contentId だけ変えた新しいインスタンスを作る）
3. 同じフィールドを持つ2つの Content が等値（==）であること
4. ContentType のパターンマッチが全ケースを網羅していること
   - Movie   → "映画" を返す
   - Series(3) → "シリーズ(3シーズン)" を返す
   - Live    → "ライブ" を返す
```

### Part2：集計処理のテスト

`AggregatorSpec.scala` を作成し、以下をテストする。

```
1. 視聴が2件・いいねが1件のとき likeRate が 50.00 になること
2. 視聴が0件のとき likeRate が 0.00 になること（ゼロ除算しないこと）
3. いいねが0件の contentId が集計結果に含まれること（likeRate=0.00）
4. 存在しない contentId の視聴件数が 0 であること
```

### Part3：エラーハンドリングのテスト

`ErrorHandlingSpec.scala` を作成し、以下をテストする。

```
1. findContent で存在する contentId を渡すと Some(content) が返ること
2. findContent で存在しない contentId を渡すと None が返ること
3. calcLikeRate で視聴数が0のとき Left が返ること
4. calcLikeRate でいいね数が視聴数を超えるとき Left が返ること
5. calcLikeRate で正常なケースのとき Right(正しいいいね率) が返ること
```

### Part4：境界値テスト

以下の境界値ケースを1つ以上テストする。

```
- 視聴 = 1、いいね = 1 のとき likeRate = 100.00
- 再生時間 = 0 のコンテンツが存在するケース
- 空のリストを渡したときの集計結果
```

---

## 完了条件

- [ ] `sbt test` で全テストが通る
- [ ] Part1の4つがすべてテストされている
- [ ] Part2の4つがすべてテストされている
- [ ] Part3の5つがすべてテストされている
- [ ] Part4の境界値テストが1つ以上ある
- [ ] テスト名が日本語で何をテストしているかわかる

---

## 詰まったときの調べ方

| 詰まりポイント | 調べる場所 |
|-------------|-----------|
| ScalaTest の基本 | 実践Scala入門 第7章 / レシピ217〜220 |
| shouldBe / should equal | `ScalaTest matchers` |
| Option のテスト | `ScalaTest Option shouldBe Some` |
| Either のテスト | `ScalaTest Either isRight` |
| テストデータの共有 | `ScalaTest beforeEach` |

<details>
<summary>ヒント1：ScalaTest の基本構造</summary>

```scala
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ContentModelsSpec extends AnyFlatSpec with Matchers {
  "Content" should "正しく生成できること" in {
    val c = Content("ct_001", "サンプル映画A", Movie, 5400)
    c.id shouldBe "ct_001"
  }
}
```

</details>

<details>
<summary>ヒント2：Option のテスト</summary>

```scala
result shouldBe Some(expected)
result shouldBe None
result.isDefined shouldBe true
```

</details>

<details>
<summary>ヒント3：Either のテスト</summary>

```scala
result.isRight shouldBe true
result shouldBe Right(50.0)
result.isLeft shouldBe true
```

</details>
