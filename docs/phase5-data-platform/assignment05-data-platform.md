# Phase5 課題 - データ基盤・統計・DS連携

**目安時間**: 10〜14時間（step1〜5）
**前提**: Phase1〜4が完了していること

---

## Step1：Databricks / Delta Lake ハンズオン

### 読む箇所（実装前に必ず読む）

- [Databricks とは - 公式ドキュメント](https://docs.databricks.com/en/introduction/index.html)（概要ページのみ）
- [Delta Lake の概要](https://docs.delta.io/latest/delta-intro.html)（Getting Started まで）

### 環境セットアップ

Databricks Community Edition（無料）でアカウントを作成する。

```
1. https://community.cloud.databricks.com/ にアクセス
2. 「Sign up」→「Get started with Community Edition」を選択
3. Cluster を新規作成（Runtime: 14.x LTS, Single Node）
4. Notebook を新規作成（言語: Python）
```

### ハンズオン1：Delta Lake の基本操作

以下のコードをノートブックのセルに順番に貼り付けて実行し、
各セルの出力を確認しながら Delta Lake の動作を理解する。

```python
# セル1: サンプルデータの作成
from pyspark.sql import SparkSession
from pyspark.sql.functions import col, to_date, lit
from datetime import date

spark = SparkSession.builder.getOrCreate()

# 広告インプレッションのサンプルデータ
data = [
    ("2024-01-01", "camp_001", "seg_001", "display", 10000, 150, 3, 45000.0),
    ("2024-01-01", "camp_001", "seg_002", "video",    8000, 200, 8, 60000.0),
    ("2024-01-02", "camp_001", "seg_001", "display", 12000, 180, 5, 54000.0),
    ("2024-01-02", "camp_002", "seg_003", "search",   5000, 400, 20, 80000.0),
    ("2024-01-03", "camp_002", "seg_003", "search",   6000, 450, 22, 90000.0),
]

columns = ["event_date", "campaign_id", "segment_id", "ad_network",
           "impressions", "clicks", "conversions", "spend"]

df = spark.createDataFrame(data, columns)
df.show()
```

```python
# セル2: Delta テーブルとして保存
delta_path = "/tmp/ad_impressions_delta"

df.write \
  .format("delta") \
  .mode("overwrite") \
  .save(delta_path)

print("Delta テーブルを保存しました")
```

```python
# セル3: Delta テーブルから読み込み・集計
df_delta = spark.read.format("delta").load(delta_path)

from pyspark.sql.functions import sum as spark_sum, round as spark_round

result = df_delta.groupBy("campaign_id", "ad_network") \
    .agg(
        spark_sum("impressions").alias("total_impressions"),
        spark_sum("clicks").alias("total_clicks"),
        spark_sum("spend").alias("total_spend")
    ) \
    .withColumn("ctr", spark_round(col("total_clicks") / col("total_impressions") * 100, 2))

result.show()
```

```python
# セル4: Delta の「タイムトラベル」機能を体験する
# レコードを追加してから、追加前の状態を参照する

new_data = [
    ("2024-01-04", "camp_001", "seg_001", "display", 9000, 130, 4, 39000.0),
]
df_new = spark.createDataFrame(new_data, columns)
df_new.write.format("delta").mode("append").save(delta_path)

# 追加後
print("=== 追加後 ===")
spark.read.format("delta").load(delta_path).count()

# 追加前（バージョン0）
print("=== 追加前（バージョン0）===")
spark.read.format("delta").option("versionAsOf", 0).load(delta_path).count()
```

```python
# セル5: UPSERT（MERGE）操作
from delta.tables import DeltaTable

delta_table = DeltaTable.forPath(spark, delta_path)

# camp_001 / seg_001 の spend を更新 + 新レコードを追加するケース
updates = [
    ("2024-01-01", "camp_001", "seg_001", "display", 10000, 150, 3, 50000.0),  # spend 更新
    ("2024-01-05", "camp_003", "seg_004", "video", 3000, 60, 1, 15000.0),       # 新規追加
]
df_updates = spark.createDataFrame(updates, columns)

delta_table.alias("target").merge(
    df_updates.alias("source"),
    "target.event_date = source.event_date AND target.campaign_id = source.campaign_id AND target.segment_id = source.segment_id"
).whenMatchedUpdateAll() \
 .whenNotMatchedInsertAll() \
 .execute()

print("MERGE 完了")
spark.read.format("delta").load(delta_path).orderBy("event_date", "campaign_id").show()
```

### 確認問題（ノートブック内にコメントで回答を書く）

```
Q1: セル4のタイムトラベルは、adtech のどんな場面で役立つか（具体例を1つ）

Q2: セル5の MERGE は、Step2（ETL深化）の「冪等性」とどう関係するか

Q3: Delta Lake を使わず通常の Parquet で保存した場合、
    MERGE や タイムトラベル はどうやって実現するか（できるか・できないか含め）
```

### 完了条件

- [ ] Community Edition でノートブックが実行できる
- [ ] セル1〜5がすべてエラーなく実行できる
- [ ] Q1〜Q3への回答がコメントで書かれている

---

## Step2：Snowflake vs BigQuery vs Databricks の比較演習

### 読む箇所

[Snowflake とは（公式）](https://www.snowflake.com/en/data-cloud/platform/) の概要（10分）。

### ハンズオン：BigQuery サンドボックスで実行比較

[BigQuery サンドボックス](https://cloud.google.com/bigquery/docs/sandbox)（無料・カード不要）を使う。

```
1. https://console.cloud.google.com/bigquery にアクセス
2. Google アカウントでログイン
3. プロジェクトを新規作成（例: adtech-training）
4. 以下のクエリを実行する
```

**実習クエリ1：公開データセットで集計**

```sql
-- Google の公開データセット（chicago_taxi_trips）を使って
-- 日別・支払い方法別の集計を練習する
SELECT
  DATE(trip_start_timestamp)  AS trip_date,
  payment_type,
  COUNT(*)                    AS trip_count,
  ROUND(AVG(fare), 2)         AS avg_fare,
  ROUND(SUM(fare), 2)         AS total_fare
FROM
  `bigquery-public-data.chicago_taxi_trips.taxi_trips`
WHERE
  trip_start_timestamp BETWEEN '2023-01-01' AND '2023-01-31'
  AND fare > 0
GROUP BY 1, 2
ORDER BY 1, total_fare DESC
```

**実習クエリ2：ウィンドウ関数で前日比**

```sql
WITH daily_totals AS (
  SELECT
    DATE(trip_start_timestamp) AS trip_date,
    COUNT(*)                   AS daily_trips,
    ROUND(SUM(fare), 2)        AS daily_revenue
  FROM
    `bigquery-public-data.chicago_taxi_trips.taxi_trips`
  WHERE
    trip_start_timestamp BETWEEN '2023-01-01' AND '2023-01-31'
    AND fare > 0
  GROUP BY 1
)
SELECT
  trip_date,
  daily_trips,
  daily_revenue,
  LAG(daily_revenue) OVER (ORDER BY trip_date)  AS prev_day_revenue,
  ROUND(
    (daily_revenue - LAG(daily_revenue) OVER (ORDER BY trip_date))
    / LAG(daily_revenue) OVER (ORDER BY trip_date) * 100,
    2
  )                                              AS revenue_growth_pct,
  SUM(daily_revenue) OVER (ORDER BY trip_date ROWS UNBOUNDED PRECEDING) AS cumulative_revenue
FROM daily_totals
ORDER BY trip_date
```

### 比較まとめ表を自分で埋める

以下の表を `docs/phase5-data-platform/` に `comparison.md` として作成し、
調べた内容・ハンズオンで感じたことを自分の言葉で埋める。

| 観点 | BigQuery | Snowflake | Databricks |
|------|---------|-----------|------------|
| 主な用途 | | | |
| 課金モデル | | | |
| MLとの親和性 | | | |
| ストリーミング対応 | | | |
| 今回の案件での想定役割 | | | |

### 完了条件

- [ ] BigQuery サンドボックスで2つのクエリが実行できる
- [ ] `comparison.md` が自分の言葉で埋まっている
- [ ] Snowflake・Databricks を使わない理由が説明できる（今回の案件の文脈で）

---

## Step3：統計学ハンズオン（adtech 文脈）

### 環境セットアップ

Google Colab（無料）を使う。
https://colab.research.google.com/ で新しいノートブックを作成する。

### ハンズオン：CTR分析を Python で実装する

```python
# セル1: ライブラリインポートとサンプルデータ生成
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from scipy import stats

np.random.seed(42)

# キャンペーンごとの1日あたりCTRデータ（30日分）
# 実際のadtechでは CTR は 0.5〜3% 程度
campaign_a_ctr = np.random.normal(loc=0.015, scale=0.003, size=30).clip(0.001, 0.1)
campaign_b_ctr = np.random.normal(loc=0.018, scale=0.004, size=30).clip(0.001, 0.1)

df = pd.DataFrame({
    'day': range(1, 31),
    'campaign_a': campaign_a_ctr,
    'campaign_b': campaign_b_ctr
})

print(df.describe())
```

```python
# セル2: 基本統計量の計算と解釈
for name, data in [('Campaign A', campaign_a_ctr), ('Campaign B', campaign_b_ctr)]:
    print(f"\n=== {name} ===")
    print(f"平均CTR  : {data.mean():.4f} ({data.mean()*100:.2f}%)")
    print(f"標準偏差 : {data.std():.4f}")
    print(f"変動係数 : {data.std()/data.mean():.4f}  ← 安定性の指標（低いほど安定）")
    print(f"95%信頼区間: {stats.t.interval(0.95, len(data)-1, loc=data.mean(), scale=stats.sem(data))}")
```

```python
# セル3: 可視化
fig, axes = plt.subplots(1, 2, figsize=(12, 4))

# 時系列
axes[0].plot(df['day'], df['campaign_a'], label='Campaign A', marker='o', markersize=3)
axes[0].plot(df['day'], df['campaign_b'], label='Campaign B', marker='s', markersize=3)
axes[0].set_xlabel('Day')
axes[0].set_ylabel('CTR')
axes[0].set_title('Daily CTR Trend')
axes[0].legend()
axes[0].yaxis.set_major_formatter(plt.FuncFormatter(lambda x, _: f'{x*100:.1f}%'))

# ヒストグラム
axes[1].hist(campaign_a_ctr * 100, alpha=0.6, label='Campaign A', bins=10)
axes[1].hist(campaign_b_ctr * 100, alpha=0.6, label='Campaign B', bins=10)
axes[1].set_xlabel('CTR (%)')
axes[1].set_ylabel('Frequency')
axes[1].set_title('CTR Distribution')
axes[1].legend()

plt.tight_layout()
plt.show()
```

```python
# セル4: t検定で有意差を検証
t_stat, p_value = stats.ttest_ind(campaign_a_ctr, campaign_b_ctr)

print(f"t統計量 : {t_stat:.4f}")
print(f"p値     : {p_value:.4f}")
print()
if p_value < 0.05:
    print("✅ p < 0.05: Campaign BはAより統計的に有意にCTRが高い")
    print(f"   差分: {(campaign_b_ctr.mean() - campaign_a_ctr.mean())*100:.3f}%ポイント")
else:
    print("❌ p >= 0.05: 有意差なし。サンプルが足りないか、実際に差がない")
```

```python
# セル5: サンプルサイズの計算（実務で使う公式）
from statsmodels.stats.power import TTestIndPower

analysis = TTestIndPower()

# 「0.3%ポイントの差を検出したい」「有意水準5%」「検出力80%」の場合
baseline_ctr = 0.015
expected_lift = 0.003   # 検出したい差（0.3%ポイント）
pooled_std = np.mean([campaign_a_ctr.std(), campaign_b_ctr.std()])

effect_size = expected_lift / pooled_std
required_n = analysis.solve_power(
    effect_size=effect_size,
    alpha=0.05,
    power=0.80,
    alternative='two-sided'
)

print(f"効果量     : {effect_size:.4f}")
print(f"必要サンプル数（片群）: {required_n:.0f} 件")
print(f"必要サンプル数（合計）: {required_n*2:.0f} 件")
print()
print("→ このサンプル数に達するまでA/Bテストを継続する必要がある")
```

### 確認問題（ノートブック内にコメントで回答）

```
Q1: セル2の「変動係数」はadtechで何を意味するか。
    変動係数が高いキャンペーンと低いキャンペーンでは、
    どちらの配信が「安定している」と言えるか

Q2: セル4の p値が 0.08 だった場合、DSから
    「有意差あり」と報告を受けたらどう判断するか

Q3: セル5でサンプルサイズが 50,000 件必要という結果が出た。
    1日あたりのインプレッションが 3,000 件の場合、何日テストが必要か
```

### 完了条件

- [ ] Colab でセル1〜5がすべて実行できる
- [ ] グラフが表示される
- [ ] Q1〜Q3への回答がコメントで書かれている
- [ ] p値と有意差の関係が自分の言葉で説明できる

---

## Step4：A/Bテスト設計・実装演習

### ハンズオン：A/Bテスト結果の分析レポートを Python で作る

前のステップの Colab ノートブックに続けて実装する。

```python
# セル6: 現実的なA/Bテストシミュレーション
# ターゲティングロジック A（purchase-data）vs B（purchase-data + carrier-data）

np.random.seed(123)
n_users = 50000   # 各群のユーザー数

# A群: CVR 2.1%
group_a_conversions = np.random.binomial(1, 0.021, n_users)
# B群: CVR 2.5%
group_b_conversions = np.random.binomial(1, 0.025, n_users)

a_cvr = group_a_conversions.mean()
b_cvr = group_b_conversions.mean()

print(f"A群 CVR: {a_cvr:.4f} ({a_cvr*100:.2f}%)")
print(f"B群 CVR: {b_cvr:.4f} ({b_cvr*100:.2f}%)")
print(f"リフト  : {(b_cvr - a_cvr) / a_cvr * 100:.2f}%")
```

```python
# セル7: 二項検定で有意差検証
from statsmodels.stats.proportion import proportions_ztest

count = np.array([group_b_conversions.sum(), group_a_conversions.sum()])
nobs  = np.array([n_users, n_users])

z_stat, p_value = proportions_ztest(count, nobs)

print(f"z統計量: {z_stat:.4f}")
print(f"p値    : {p_value:.6f}")
print()
if p_value < 0.05:
    print(f"✅ 統計的に有意（p={p_value:.4f} < 0.05）")
    print(f"   B群の方が CVR が {(b_cvr - a_cvr)*100:.3f}%ポイント高い")
    print(f"   → Bのターゲティングロジックを採用すると推奨できる")
else:
    print(f"❌ 有意差なし（p={p_value:.4f}）")
```

```python
# セル8: ビジネスインパクト試算
budget = 10_000_000  # 1000万円
cpc = 200            # 1クリックあたりのコスト（円）
estimated_clicks = budget / cpc

impact_a = estimated_clicks * a_cvr
impact_b = estimated_clicks * b_cvr
incremental = impact_b - impact_a

print(f"=== ビジネスインパクト試算 ===")
print(f"予算          : ¥{budget:,}")
print(f"推定クリック数 : {estimated_clicks:,.0f} 件")
print()
print(f"A群（現行）推定CV数 : {impact_a:,.0f} 件")
print(f"B群（改善）推定CV数 : {impact_b:,.0f} 件")
print(f"増分CV数            : +{incremental:,.0f} 件")
print()
print(f"→ Bを採用することで、同じ予算で {incremental:.0f} 件多くのCVが見込める")
```

### 確認問題

```
Q1: セル7で p値が非常に小さい（例: 0.0001）場合、
    「効果が非常に大きい」と言えるか。p値は何を表し、何を表さないか

Q2: セル8の試算には、どんな前提条件が含まれているか。
    実際に「CVが増える」と確信するには何を確認する必要があるか

Q3: A/Bテスト期間中に「明らかにBが良さそう」に見えたとして、
    途中でテストを打ち切って B を採用すべきか。その理由も述べよ
```

### 完了条件

- [ ] セル6〜8がエラーなく実行できる
- [ ] ビジネスインパクト試算の出力が正しい
- [ ] Q1〜Q3への回答がコメントで書かれている

---

## Step5：成果物まとめ

以下のファイルを `docs/phase5-data-platform/` に作成する。

**`review.md`（自己評価シート）**

```markdown
## Phase5 自己評価

### Step1: Databricks / Delta Lake
- 実行できたセル: 1 / 2 / 3 / 4 / 5
- Delta Lake を使う理由を一言で言うと：

### Step2: データ基盤比較
- comparison.md を作成した: Yes / No
- 今回の案件で BigQuery を選ぶ理由：

### Step3: 統計学
- 一番理解が難しかった概念：
- p値を自分の言葉で説明すると：

### Step4: A/Bテスト
- セル6〜8を実行できた: Yes / No
- 「p値が小さい ≠ 効果が大きい」を説明すると：

### 全体を通じて
- DSとの会話で使えそうな言葉・概念：
- 次のPhaseに向けて補完したい知識：
```

### 完了条件

- [ ] `review.md` が作成されている
- [ ] 全Step の完了条件がチェックされている
