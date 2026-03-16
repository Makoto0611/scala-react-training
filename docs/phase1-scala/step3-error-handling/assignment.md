# Step3 - エラーハンドリング

**参照書籍**: 実践Scala入門 第3章
**目安時間**: 4〜6時間

---

## 読む箇所

実践Scala入門 第3章を全て読む。

- Option（「値がないかもしれない」を表す）
- Either（失敗した理由を示す）
- Try（Option/Eitherと同じ感覚で例外を扱う）

補助として「関数プログラミング実践入門」第1章「NULLの危険性・Maybe型」を読むと
null ではなく Option を使う理由の根本が理解できる。

---

## 課題

step2の集計処理に、適切なエラーハンドリングを追加する。

### Part1：Option を使う

以下の関数を実装する。

```
関数名：findAd
引数  ：adId: String, ads: List[Ad]
戻り値：Option[Ad]
仕様  ：ads の中に adId と一致する Ad があれば Some(ad) を返す。なければ None を返す。
```

この関数を使い、以下を出力する処理を実装する。

```
ad_001 が見つかりました: テスト広告A
ad_999 は見つかりませんでした
```

`find` メソッドを使わずに `filter` と `headOption` で実装するバージョンと、
`find` で実装するバージョンの両方を作ること。

### Part2：Either でエラーハンドリングする

CTR計算に Either を使ったバージョンを実装する。

```
関数名：calcCtr
引数  ：impressions: Int, clicks: Int
戻り値：Either[String, Double]
仕様  ：
  - impressions が 0 の場合は Left("インプレッション数が0のためCTRを計算できません")
  - clicks が impressions より大きい場合は Left("クリック数がインプレッション数を超えています")
  - 正常な場合は Right(CTR値) を返す（0.0〜100.0の値）
```

結果を match で処理して、Left / Right それぞれを適切に出力すること。

### Part3：Try でファイル読み込みをラップする

以下のダミー関数をベースに実装する。

```scala
def readJsonFile(path: String): String = {
  // 存在しないパスを渡すと例外が発生する想定
  scala.io.Source.fromFile(path).mkString
}
```

この関数を Try でラップし、
- 成功した場合は内容を出力する
- 失敗した場合は `"ファイルの読み込みに失敗しました: " + エラーメッセージ` を出力する

存在するファイルと存在しないファイルの両方で動作確認すること。

### Part4：Option / Either / Try の使い分け

以下のケースそれぞれについて、自分の言葉でコメントを書く（コードファイルのコメントとして残す）。

```
Q1: findAd の戻り値を Either[String, Ad] にするメリット・デメリットは何か
Q2: calcCtr の戻り値を Option[Double] にした場合と Either[String, Double] にした場合、
    呼び出し元でどんな違いが出るか
Q3: Try を使わずに try-catch で書いた場合と比べて何が変わるか
```

---

## 完了条件

- [ ] findAd が filter+headOption バージョンと find バージョンで動作する
- [ ] calcCtr が3つのケース（0除算・クリック超過・正常）を正しく処理する
- [ ] Try でファイル読み込みが成功・失敗の両方で動作する
- [ ] Part4のコメントが自分の言葉で書かれている
- [ ] `null` を使っていない
- [ ] try-catch を使っていない（Try / Either / Option のみ）

---

## 詰まったときの調べ方

| 詰まりポイント | 調べる場所 |
|-------------|-----------|
| Option の基本 | 実践Scala入門 第3章 / レシピ050 |
| Option を match で処理 | `Scala Option match` |
| Option の map / getOrElse | レシピ051 |
| Either の基本 | 実践Scala入門 第3章 / レシピ052 |
| Try の使い方 | 実践Scala入門 第3章 |
| find の使い方 | レシピ108 |

<details>
<summary>ヒント1：Option を match で処理する</summary>

```scala
findAd("ad_001", ads) match {
  case Some(ad) => println(s"見つかりました: ${ad.name}")
  case None     => println("見つかりませんでした")
}
```

</details>

<details>
<summary>ヒント2：Either を match で処理する</summary>

```scala
calcCtr(impressions, clicks) match {
  case Right(ctr) => println(f"CTR: $ctr%.2f%%")
  case Left(msg)  => println(s"エラー: $msg")
}
```

</details>

<details>
<summary>ヒント3：Try の構造</summary>

```scala
import scala.util.{Try, Success, Failure}

Try(readJsonFile(path)) match {
  case Success(content) => println(content)
  case Failure(e)       => println(s"失敗: ${e.getMessage}")
}
```

</details>
