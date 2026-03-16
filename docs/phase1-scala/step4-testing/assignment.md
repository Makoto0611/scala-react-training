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

`AdModelsSpec.scala` を作成し、以下をテストする。

```
1. Ad の case class が正しく生成できること
2. Impression の copy が正しく動作すること（adId だけ変えた新しいインスタンスを作る）
3. 同じフィールドを持つ2つの Ad が等値（==）であること
4. AdType のパターンマッチが全ケースを網羅していること
   - Banner → "バナー" を返す
   - Video(30) → "動画(30s)" を返す
   - Native → "ネイティブ" を返す
```

### Part2：集計処理のテスト

`AggregatorSpec.scala` を作成し、以下をテストする。

```
1. インプレッションが2件・クリックが1件のとき CTR が 50.00 になること
2. インプレッションが0件のとき CTR が 0.00 になること（ゼロ除算しないこと）
3. クリックが0件の adId が集計結果に含まれること（CTR=0.00）
4. 存在しない adId のインプレッション件数が 0 であること
```

### Part3：エラーハンドリングのテスト

`ErrorHandlingSpec.scala` を作成し、以下をテストする。

```
1. findAd で存在する adId を渡すと Some(ad) が返ること
2. findAd で存在しない adId を渡すと None が返ること
3. calcCtr でインプレッションが0のとき Left が返ること
4. calcCtr でクリック数がインプレッション数を超えるとき Left が返ること
5. calcCtr で正常なケースのとき Right(正しいCTR値) が返ること
```

### Part4：境界値テスト

以下の境界値ケースを1つ以上テストする。

```
- インプレッション = 1、クリック = 1 のとき CTR = 100.00
- 予算 = 0 の広告が存在するケース
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

class AdModelsSpec extends AnyFlatSpec with Matchers {
  "Ad" should "正しく生成できること" in {
    val ad = Ad("ad_001", "テスト広告A", Banner, 50000)
    ad.id shouldBe "ad_001"
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
