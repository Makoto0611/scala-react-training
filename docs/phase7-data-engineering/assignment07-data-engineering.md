# Phase7 課題 - データエンジニアリング実践

**目安時間**: 10〜12時間（step1〜4）
**前提**: Phase5が完了していること（BigQuery サンドボックスが使える状態）

---

## Step1：BigQuery 実践SQL（adtech文脈の集計パターン）

### 環境セットアップ

Phase5 Step2 で作成した BigQuery サンドボックスのプロジェクトを使う。
まず以下のコマンドでサンプルテーブルを作成する。

```sql
-- データセット作成
CREATE SCHEMA IF NOT EXISTS adtech_training;

-- 広告インプレッションログテーブル
CREATE OR REPLACE TABLE adtech_training.ad_impressions (
  event_date    DATE,
  campaign_id   STRING,
  segment_id    STRING,
  ad_network    STRING,
  impressions   INT64,
  clicks        INT64,
  conversions   INT64,
  spend         FLOAT64
);

-- セグメント定義テーブル
CREATE OR REPLACE TABLE adtech_training.segments (
  segment_id      STRING,
  segment_name    STRING,
  data_source     STRING,
  estimated_reach INT64
);
```

```sql
-- サンプルデータ投入
INSERT INTO adtech_training.ad_impressions VALUES
  ('2024-01-01', 'camp_001', 'seg_001', 'display',  10000, 150,  3,  45000.0),
  ('2024-01-01', 'camp_001', 'seg_002', 'video',     8000, 200,  8,  60000.0),
  ('2024-01-01', 'camp_002', 'seg_003', 'search',    5000, 400, 20,  80000.0),
  ('2024-01-02', 'camp_001', 'seg_001', 'display',  12000, 180,  5,  54000.0),
  ('2024-01-02', 'camp_001', 'seg_002', 'video',     9000, 230, 10,  69000.0),
  ('2024-01-02', 'camp_002', 'seg_003', 'search',    6000, 450, 22,  90000.0),
  ('2024-01-03', 'camp_001', 'seg_001', 'display',  11000, 165,  4,  49500.0),
  ('2024-01-03', 'camp_002', 'seg_003', 'search',    5500, 420, 18,  82500.0),
  ('2024-01-03', 'camp_003', 'seg_004', 'video',     3000,  60,  1,  15000.0),
  ('2024-01-04', 'camp_001', 'seg_001', 'display',   9000, 130,  2,  39000.0),
  ('2024-01-04', 'camp_002', 'seg_003', 'search',    7000, 500, 25, 105000.0),
  ('2024-01-05', 'camp_001', 'seg_002', 'video',    10000, 250, 12,  75000.0),
  ('2024-01-05', 'camp_003', 'seg_004', 'display',   4000,  80,  2,  20000.0);

INSERT INTO adtech_training.segments VALUES
  ('seg_001', 'F1層（20-34歳女性）',   'purchase-data',          850000),
  ('seg_002', 'M1層（20-34歳男性）',   'purchase-data',          720000),
  ('seg_003', '高購買意向層',           'carrier-audience-data', 1200000),
  ('seg_004', 'リターゲティング対象',   'carrier-audience-data',  300000);
```

### 課題1：基本集計（日別・ad_network別）

以下の要件を満たすクエリを書いて実行する。
実行結果のスクリーンショットを `docs/phase7-data-engineering/results/` に保存する。

```
要件:
- 全期間の日別・ad_network別の集計
- 出力カラム:
    event_date, ad_network,
    total_impressions, total_clicks, total_conversions, total_spend,
    ctr（%・小数点2桁）, cvr（%・小数点2桁）, cpa（円・整数）
- CTR  = clicks / impressions × 100
- CVR  = conversions / clicks × 100（clicks が 0 のとき NULL）
- CPA  = spend / conversions（conversions が 0 のとき NULL）
- event_date 降順、同日内は total_spend 降順
```

<details>
<summary>ヒント：ゼロ除算の回避</summary>

```sql
ROUND(SAFE_DIVIDE(clicks, impressions) * 100, 2) AS ctr
```

</details>

### 課題2：ウィンドウ関数（前日比・累計・ランキング）

```
要件:
- キャンペーンごとの日別 spend・前日比・累計・当日内ランキング
- 出力カラム:
    campaign_id, event_date,
    daily_spend,
    prev_day_spend,
    spend_growth_pct（前日比 %・前日なし = NULL）,
    cumulative_spend（累計）,
    daily_rank（その日の spend 順位、1位が最大）
- campaign_id 昇順、同 campaign_id 内は event_date 昇順
```

<details>
<summary>ヒント：ウィンドウ関数の構造</summary>

```sql
LAG(spend) OVER (PARTITION BY campaign_id ORDER BY event_date) AS prev_day_spend

SUM(spend) OVER (
  PARTITION BY campaign_id
  ORDER BY event_date
  ROWS UNBOUNDED PRECEDING
) AS cumulative_spend

RANK() OVER (PARTITION BY event_date ORDER BY spend DESC) AS daily_rank
```

</details>

### 課題3：セグメント別パフォーマンス分析 + JOIN

```
要件:
- segments テーブルと ad_impressions を JOIN する
- 全セグメントの合計成果・CPA・リーチ効率（impressions / estimated_reach）を出す
- CPA 順位（低い方が良い）を付ける（NULL は末尾）
- 出力カラム:
    segment_id, segment_name, data_source, estimated_reach,
    total_spend, total_conversions, cpa, cpa_rank, reach_efficiency
- cpa_rank: CPA が低い順に 1, 2, 3...（NULL は除外してから順位付け）
```

### 課題4：サブクエリ・CTE を使ったレポートクエリ

実務でよく使う「日別KPIサマリー + 累計 + 当月平均比較」を1クエリで書く。

```
要件:
- 以下を1クエリにまとめる（CTEを3つ以上使うこと）

  CTE1: 日別合計（全キャンペーン）
    event_date, total_spend, total_impressions, total_clicks, total_cv

  CTE2: 累計（CTE1から）
    event_date, cumulative_spend, cumulative_cv, cumulative_cpa

  CTE3: 期間平均（CTE1から）
    avg_daily_spend, avg_daily_cv, avg_cpa

  最終SELECT:
    CTE1 + CTE2 を JOIN し、CTE3 の値を全行に付与
    → 各日の実績 vs 期間平均の比較が1テーブルで見られる形

- event_date 降順
```

<details>
<summary>ヒント：CTEの基本構造</summary>

```sql
WITH
cte1 AS (
  SELECT ...
),
cte2 AS (
  SELECT ...
  FROM cte1
),
cte3 AS (
  SELECT AVG(...) FROM cte1
)
SELECT
  c1.*,
  c2.cumulative_spend,
  c3.avg_daily_spend
FROM cte1 c1
JOIN cte2 c2 USING (event_date)
CROSS JOIN cte3 c3
ORDER BY c1.event_date DESC
```

</details>

### 完了条件

- [ ] データセット・テーブルが作成できている
- [ ] 課題1〜4がすべて実行できる
- [ ] `results/` にスクリーンショットが保存されている
- [ ] 課題4でCTEが3つ以上使われている

---

## Step2：ETLパイプライン本番運用パターン（Airflow深化）

### 前提

Phase5 Step1 で Airflow の基本概念（DAG・ETL・スケジューリング）を学んだ。
本stepでは以下を追加実装する：リトライ・アラート・冪等性・増分更新。

### 環境セットアップ

```bash
# Airflow をローカルで起動（Docker を使う）
mkdir airflow-phase7 && cd airflow-phase7

curl -LfO 'https://airflow.apache.org/docs/apache-airflow/stable/docker-compose.yaml'
mkdir -p ./dags ./logs ./plugins

echo "AIRFLOW_UID=$(id -u)" > .env
docker compose up airflow-init
docker compose up -d

# ブラウザで http://localhost:8080 を開く
# ユーザー: airflow / パスワード: airflow
```

### ハンズオン1：基本ETL DAGを本番運用パターンに改修する

`dags/impression_etl_dag.py` を以下の仕様で作成する。

```python
from airflow import DAG
from airflow.operators.python import PythonOperator
from airflow.utils.dates import days_ago
from datetime import timedelta
import logging

# ① コールバック関数（タスク失敗時に呼ばれる）
def on_failure_callback(context):
    """失敗時にログにエラー詳細を出力する"""
    task_instance = context['task_instance']
    logging.error(
        f"タスク失敗: DAG={context['dag'].dag_id}, "
        f"Task={task_instance.task_id}, "
        f"実行日={context['ds']}, "
        f"エラー={context.get('exception')}"
    )
    # 本番では Slack / PagerDuty 通知をここに書く

# ② DAG 定義
with DAG(
    dag_id='impression_etl_daily',
    schedule_interval='0 2 * * *',   # 毎日 AM2:00
    start_date=days_ago(1),
    catchup=False,
    default_args={
        'retries': 3,
        'retry_delay': timedelta(minutes=5),
        'retry_exponential_backoff': True,  # リトライ間隔を指数関数的に延ばす
        'on_failure_callback': on_failure_callback,
    },
    tags=['adtech', 'etl', 'daily'],
) as dag:

    # ③ Extract: 前日分のデータのみ取得（増分更新）
    def extract(**context):
        target_date = context['ds']   # 実行日（YYYY-MM-DD）
        logging.info(f"Extract 開始: {target_date}")

        # 実際の実装では BigQuery や DB から取得する
        # ここではダミーデータを返す
        data = [
            {"event_date": target_date, "campaign_id": "camp_001",
             "impressions": 10000, "clicks": 150, "conversions": 3, "spend": 45000.0},
            {"event_date": target_date, "campaign_id": "camp_002",
             "impressions": 5000,  "clicks": 400, "conversions": 20, "spend": 80000.0},
        ]

        # XCom で次のタスクにデータを渡す
        context['ti'].xcom_push(key='raw_data', value=data)
        logging.info(f"Extract 完了: {len(data)} 件")

    # ④ Transform: 集計・加工
    def transform(**context):
        target_date = context['ds']
        raw_data = context['ti'].xcom_pull(key='raw_data', task_ids='extract')

        transformed = []
        for row in raw_data:
            clicks = row['clicks']
            impressions = row['impressions']
            conversions = row['conversions']
            spend = row['spend']

            transformed.append({
                **row,
                'ctr': round(clicks / impressions * 100, 4) if impressions > 0 else None,
                'cvr': round(conversions / clicks * 100, 4) if clicks > 0 else None,
                'cpa': round(spend / conversions, 2) if conversions > 0 else None,
            })

        context['ti'].xcom_push(key='transformed_data', value=transformed)
        logging.info(f"Transform 完了: {len(transformed)} 件")

    # ⑤ Load: 冪等性を持たせた書き込み（MERGE相当）
    def load(**context):
        target_date = context['ds']
        data = context['ti'].xcom_pull(key='transformed_data', task_ids='transform')

        # 本番では BigQuery の MERGE 文を実行する
        # ここでは「同じ日付のデータを DELETE してから INSERT」でシミュレート
        logging.info(f"Load 開始: {target_date} のデータを {len(data)} 件ロード")
        logging.info(f"  → 既存データを削除してから INSERT（冪等性を確保）")

        for row in data:
            logging.info(f"  INSERT: {row}")

        logging.info(f"Load 完了")

    extract_task   = PythonOperator(task_id='extract',   python_callable=extract,   provide_context=True)
    transform_task = PythonOperator(task_id='transform', python_callable=transform, provide_context=True)
    load_task      = PythonOperator(task_id='load',      python_callable=load,      provide_context=True)

    extract_task >> transform_task >> load_task
```

### ハンズオン2：DAGの動作確認

```bash
# DAG をトリガーして動作確認
docker exec -it airflow-phase7-airflow-scheduler-1 \
  airflow dags trigger impression_etl_daily

# ログ確認
docker exec -it airflow-phase7-airflow-scheduler-1 \
  airflow tasks logs impression_etl_daily extract $(date +%Y-%m-%d)
```

ブラウザ（http://localhost:8080）で以下を確認する：
- DAG が Graph View で extract → transform → load の順に表示される
- 手動トリガーで全タスクが成功する（緑）
- タスクのログに `logging.info` の出力が表示される

### ハンズオン3：失敗シミュレーション・リトライ確認

`extract` 関数に意図的なエラーを入れて、リトライ動作を確認する。

```python
def extract(**context):
    target_date = context['ds']
    # 意図的に失敗させる（テスト用）
    raise ValueError("データソースに接続できません（テスト用エラー）")
```

確認すること：
- Airflow UI でタスクが `up_for_retry` 状態になる
- 3回リトライ後に `failed` になる
- ログに `on_failure_callback` の出力が表示される

確認後、エラーコードを元に戻して再実行する。

### 確認問題

`docs/phase7-data-engineering/` に `etl-review.md` を作成して回答する。

```
Q1: Load タスクで「DELETE + INSERT」を使う理由を説明せよ。
    「INSERT のみ」にした場合、DAGが2回実行されるとどうなるか

Q2: retry_exponential_backoff=True を設定する理由は何か。
    固定の retry_delay=5分 との違いを説明せよ

Q3: XCom の使い方（xcom_push / xcom_pull）の利点と欠点を説明せよ。
    大量データ（100万件）を XCom で渡そうとするとどうなるか

Q4: catchup=False の意味を説明せよ。
    start_date を過去に設定して catchup=True にした場合、
    何が起きるか
```

### 完了条件

- [ ] Docker で Airflow が起動できる
- [ ] `impression_etl_daily` DAG が成功する（緑）
- [ ] 失敗シミュレーションでリトライが3回発生することを確認した
- [ ] `etl-review.md` の Q1〜Q4 が回答されている

---

## Step3：dbt 基礎ハンズオン

### 環境セットアップ

```bash
pip install dbt-bigquery

# プロジェクト初期化
dbt init adtech_dbt
cd adtech_dbt

# profiles.yml の設定（~/.dbt/profiles.yml）
# BigQuery サンドボックスのプロジェクトIDを設定する
```

```yaml
# ~/.dbt/profiles.yml
adtech_dbt:
  target: dev
  outputs:
    dev:
      type: bigquery
      method: oauth
      project: YOUR_PROJECT_ID   # ← Phase5 Step2 で作ったプロジェクトID
      dataset: adtech_training
      threads: 1
      timeout_seconds: 300
```

```bash
# 接続確認
dbt debug
```

### ハンズオン1：最初のモデルを作る

`models/staging/stg_ad_impressions.sql` を作成する。

```sql
-- models/staging/stg_ad_impressions.sql
{{ config(materialized='view') }}

SELECT
  event_date,
  campaign_id,
  segment_id,
  ad_network,
  impressions,
  clicks,
  conversions,
  spend,
  -- 派生カラムを追加
  SAFE_DIVIDE(clicks, impressions)     AS ctr,
  SAFE_DIVIDE(conversions, clicks)     AS cvr,
  SAFE_DIVIDE(spend, conversions)      AS cpa
FROM {{ source('adtech_training', 'ad_impressions') }}
```

`models/sources.yml` を作成する。

```yaml
version: 2

sources:
  - name: adtech_training
    database: YOUR_PROJECT_ID
    schema: adtech_training
    tables:
      - name: ad_impressions
        description: "広告インプレッションログ"
        columns:
          - name: event_date
            description: "イベント日付"
          - name: campaign_id
            description: "キャンペーンID"
          - name: impressions
            description: "インプレッション数"
            tests:
              - not_null
```

```bash
dbt run --select stg_ad_impressions
dbt test --select stg_ad_impressions
```

### ハンズオン2：マートモデルを作る（ref を使う）

`models/mart/daily_campaign_metrics.sql` を作成する。

```sql
-- models/mart/daily_campaign_metrics.sql
{{ config(materialized='table') }}

WITH base AS (
  SELECT * FROM {{ ref('stg_ad_impressions') }}
),

aggregated AS (
  SELECT
    event_date,
    campaign_id,
    SUM(impressions)   AS total_impressions,
    SUM(clicks)        AS total_clicks,
    SUM(conversions)   AS total_cv,
    SUM(spend)         AS total_spend,
    ROUND(SAFE_DIVIDE(SUM(clicks), SUM(impressions)) * 100, 2)   AS ctr_pct,
    ROUND(SAFE_DIVIDE(SUM(conversions), SUM(clicks)) * 100, 2)   AS cvr_pct,
    ROUND(SAFE_DIVIDE(SUM(spend), SUM(conversions)), 0)           AS cpa
  FROM base
  GROUP BY 1, 2
),

with_rank AS (
  SELECT
    *,
    RANK() OVER (PARTITION BY event_date ORDER BY total_spend DESC) AS spend_rank
  FROM aggregated
)

SELECT * FROM with_rank
ORDER BY event_date DESC, spend_rank
```

```bash
dbt run --select daily_campaign_metrics
```

### ハンズオン3：テストとドキュメントを追加する

`models/mart/daily_campaign_metrics.yml` を作成する。

```yaml
version: 2

models:
  - name: daily_campaign_metrics
    description: "日別・キャンペーン別の集計KPIテーブル"
    columns:
      - name: event_date
        description: "集計日"
        tests:
          - not_null
      - name: campaign_id
        description: "キャンペーンID"
        tests:
          - not_null
      - name: ctr_pct
        description: "クリック率（%）"
        tests:
          - not_null
      - name: cpa
        description: "顧客獲得単価（円）。コンバージョンが0件の場合はNULL"
```

```bash
# テスト実行
dbt test --select daily_campaign_metrics

# ドキュメント生成・確認
dbt docs generate
dbt docs serve
# → ブラウザで http://localhost:8080 を開く（lineage graph が表示される）
```

### 確認問題（`etl-review.md` に追記）

```
Q5: {{ ref('stg_ad_impressions') }} を直接テーブル名に変えると
    何が問題になるか。dbt が ref() を提供する理由を説明せよ

Q6: stg_ad_impressions を materialized='view' にして、
    daily_campaign_metrics を materialized='table' にした理由を考えよ。
    逆にした場合（staging=table, mart=view）はどうなるか

Q7: dbt docs serve で表示される lineage graph は何を表しているか。
    チームで開発するときにどう役立つか
```

### 完了条件

- [ ] `dbt debug` が成功する
- [ ] `dbt run` でモデルが2つとも作成される
- [ ] `dbt test` が全件パスする
- [ ] `dbt docs serve` でlineage graphが表示される
- [ ] Q5〜Q7 が `etl-review.md` に回答されている

---

## Step4：Tableau 連携の概念とデータソース設計

### ハンズオン：Tableau Public でのデータ可視化

[Tableau Public](https://public.tableau.com/)（無料）を使う。

```
1. https://public.tableau.com/app/discover でアカウント作成
2. Tableau Desktop Public Edition をダウンロード・インストール
3. 「データに接続」→「テキストファイル」を選択
```

Step1 課題1の実行結果をCSVでダウンロードして読み込む。

```
BigQuery → 「結果をエクスポート」→「CSVとしてダウンロード」
```

**作成するビジュアライゼーション：**

1. **折れ線グラフ**：日別の total_spend の推移（ad_network 別に色分け）
2. **棒グラフ**：ad_network 別の CTR 比較（降順ソート）
3. **散布図**：CTR（横軸）× CVR（縦軸）、バブルサイズ = total_spend

3つをダッシュボードにまとめてPublish する。

### 設計演習：Tableau 向けデータソース設計

`docs/phase7-data-engineering/` に `datasource-design.md` を作成する。

```markdown
## Tableau 向けデータソース設計書

### 要件
- キャンペーンマネージャーが毎朝9時にKPIを確認する
- フィルター：期間・キャンペーンID・ad_network・セグメント
- 更新頻度：毎日AM3時（Airflow ETL完了後）

### 設計方針

#### テーブル構成
（以下を自分で設計して記述する）

| テーブル名 | 用途 | 更新方法 | 行数見込み |
|-----------|------|---------|----------|
| | | | |

#### Tableau が参照するテーブル
（直接参照するテーブルとその理由）

#### React ダッシュボード（Phase2）との使い分け
（ユーザー・用途・更新頻度・カスタマイズ性で整理）
```

### 確認問題

```
Q8: 生ログテーブル（ad_impressions、想定1億行）に
    Tableau を直接接続するとどんな問題が起きるか。
    dbt で作成した daily_campaign_metrics を使う理由を説明せよ

Q9: Phase2 の React ダッシュボードと Tableau ダッシュボードを
    同じプロダクトに共存させる場合、どのように役割分担するか

Q10: Airflow の ETL 完了後に dbt を自動実行し、
     その後 Tableau のデータソースをリフレッシュする
     パイプラインを設計するとしたら、どんな構成になるか（図や箇条書きで）
```

### 完了条件

- [ ] Tableau Public にビジュアライゼーション3つが作成できている
- [ ] `datasource-design.md` が記述されている
- [ ] Q8〜Q10 が `etl-review.md` に回答されている

---

## 成果物まとめ

Phase7 完了時点で以下が存在すること：

```
airflow-phase7/
└── dags/
    └── impression_etl_dag.py

adtech_dbt/
├── models/
│   ├── sources.yml
│   ├── staging/
│   │   └── stg_ad_impressions.sql
│   └── mart/
│       ├── daily_campaign_metrics.sql
│       └── daily_campaign_metrics.yml
└── dbt_project.yml

docs/phase7-data-engineering/
├── results/          ← BigQuery のスクリーンショット
├── etl-review.md     ← Q1〜Q10 の回答
└── datasource-design.md
```
