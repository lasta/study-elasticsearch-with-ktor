---
title: "Quick start"
date: 2021-06-02T19:08:12+09:00
draft: false
---

参照元ドキュメント : [Official Document - Quick start](https://www.elastic.co/guide/en/elasticsearch/reference/current/getting-started.html)

# Quick start

* Elasticsearch をテスト環境にインストールし実行
* Elasticsearch にデータを追加
* データを検索およびソート
* 検索時に構造化されていないコンテンツからフィールドを抽出

## Step 1. 起動
### Elasticsearch

1. Docker Desktop を起動
2. 下記を実行

```bash
docker network create elastic
docker pull docker.elastic.co/elasticsearch/elasticsearch:7.13.0
docker run --name es01-test --net elastic -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:7.13.0
```

[http://localhost:9200/](http://localhost:9200/) へアクセスし、 JSON が返却されれば OK です。

```json
{
  "name": "187e3cc75202",
  "cluster_name": "docker-cluster",
  "cluster_uuid": "tjZJV2qWQtutpBCruoAUCA",
  "version": {
    "number": "7.13.0",
    "build_flavor": "default",
    "build_type": "docker",
    "build_hash": "5ca8591c6fcdb1260ce95b08a8e023559635c6f3",
    "build_date": "2021-05-19T22:22:26.081971330Z",
    "build_snapshot": false,
    "lucene_version": "8.8.2",
    "minimum_wire_compatibility_version": "6.8.0",
    "minimum_index_compatibility_version": "6.0.0-beta1"
  },
  "tagline": "You Know, for Search"
}
```

### Kibana

直感的な UI で Elasticsearch 内のデータを解析、表示、管理するには Kibana を利用します。

```bash
docker pull docker.elastic.co/kibana/kibana:7.13.0
docker run --name kib01-test --net elastic -p 5601:5601 -e "ELASTICSEARCH_HOSTS=http://es01-test:9200" docker.elastic.co/kibana/kibana:7.13.0
```

[http://localhost:5601/](http://localhost:5601/app/home#/) へアクセスし、 Kibana の UI が表示されれば OK です。

## Step 2. Eleasticsearch にリクエストを送ってみる
CUrl を用いるか、 Kibana の DevTools > Console からリクエストを送ります。

```shell
curl -XGET http://localhost:9200/
```

## Step 3. データを追加
JSON オブジェクト形式のドキュメントと呼ばれるデータを Elasticsearch に追加します。
ログやメトリクスなどの時系列データには、 `@timestamp` フィールドが必要になります。

### 単一のドキュメントの追加
`logs-my_app-default` index に単一のログを投入します。
`logs-my_app-default` がない場合、ビルトインの `logs-*-*` index template を用いて自動的に作成されます。

```dockerfile
POST logs-my_app-default/_doc
{
  "@timestamp": "2099-05-06T16:21:15.000Z",
  "event": {
    "original": "192.0.2.42 - - [06/May/2099:16:21:15 +0000] \"GET /images/bg.jpg HTTP/1.0\" 200 24736"
  }
}
```

レスポンスとして、 Elasticsearch が生成したドキュメントのメタデータが返却されます。

* ドキュメントが保持される backing `_index` 
    * Elasticsearch は backing index の名前を自動的に生成する
* インデックス内のドキュメントのユニークな `_id`

```json
{
  "_index" : ".ds-logs-my_app-default-2021.06.02-000001",
  "_type" : "_doc",
  "_id" : "8yp6zHkBfubf8FpwI40r",
  "_version" : 1,
  "result" : "created",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 0,
  "_primary_term" : 1
}
```

### 複数のドキュメントの追加
1リクエストで複数のドキュメントを追加するには、 bulk API を利用します。
バルクデータは NDJSON (newline-delimited JSON) で表現します。
最終行も含め、各行の末尾は改行文字 (`\n`) で終わる必要があります。

```dockerfile
PUT logs-my_app-default/_bulk
{ "create": { } }
{ "@timestamp": "2099-05-07T16:24:32.000Z", "event": { "original": "192.0.2.242 - - [07/May/2020:16:24:32 -0500] \"GET /images/hm_nbg.jpg HTTP/1.0\" 304 0" } }
{ "create": { } }
{ "@timestamp": "2099-05-08T16:25:42.000Z", "event": { "original": "192.0.2.255 - - [08/May/2099:16:25:42 +0000] \"GET /favicon.ico HTTP/1.0\" 200 3638" } }
```

## Step 4. 検索
インデックスされたドキュメントは準リアルタイムで検索可能です。
インデックスしたデータストリームを検索するには、 search API を利用します。

下記は `logs-my_app-default` 内の前エントリを取得し `@timestamp` の降順で取得するクエリです。

```dockerfile
GET logs-my_app-default/_search
{
  "query": {
    "match_all": { }
  },
  "sort": [
    {
      "@timestamp": "desc"
    }
  ]
}
```

デフォルトでは、 `hits` セクションに検索結果上位10ドキュメントが返却されます。
各 `_source` には index したオリジナルの JSON オブジェクトが格納されます。

```json
{
  "took" : 359,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 3,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [
      {
        "_index" : ".ds-logs-my_app-default-2021.06.02-000001",
        "_type" : "_doc",
        "_id" : "-Cqg5HkBfubf8FpwzI3A",
        "_score" : null,
        "_source" : {
          "@timestamp" : "2099-05-08T16:25:42.000Z",
          "event" : {
            "original" : """192.0.2.255 - - [08/May/2099:16:25:42 +0000] "GET /favicon.ico HTTP/1.0" 200 3638"""
          }
        },
        "sort" : [
          4081940742000
        ]
      },
      {
        "_index" : ".ds-logs-my_app-default-2021.06.02-000001",
        "_type" : "_doc",
        "_id" : "9yqg5HkBfubf8FpwzI28",
        "_score" : null,
        "_source" : {
          "@timestamp" : "2099-05-07T16:24:32.000Z",
          "event" : {
            "original" : """192.0.2.242 - - [07/May/2020:16:24:32 -0500] "GET /images/hm_nbg.jpg HTTP/1.0" 304 0"""
          }
        },
        "sort" : [
          4081854272000
        ]
      },
      {
        "_index" : ".ds-logs-my_app-default-2021.06.02-000001",
        "_type" : "_doc",
        "_id" : "8yp6zHkBfubf8FpwI40r",
        "_score" : null,
        "_source" : {
          "@timestamp" : "2099-05-06T16:21:15.000Z",
          "event" : {
            "original" : """192.0.2.42 - - [06/May/2099:16:21:15 +0000] "GET /images/bg.jpg HTTP/1.0" 200 24736"""
          }
        },
        "sort" : [
          4081767675000
        ]
      }
    ]
  }
}
```

### 特定のフィールドの取得
1ドキュメントのサイズが大きい場合、 `_source` 全体を解析するのはしんどいです。
レスポンスから source を除外するには、 `_source` パラメータに `false` を指定します。
その上で、 `fields` パラメータにて必要なフィールドを指定します。

```dockerfile
GET logs-my_app-default/_search
{
  "query": {
    "match_all": { }
  },
  "fields": [
    "@timestamp"
  ],
  "_source": false,
  "sort": [
    {
      "@timestamp": "desc"
    }
  ]
}
```

レスポンスには、ヒットしたドキュメントのフィールドの値が平坦な配列として含まれます。

```json
{
  "took" : 134,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 3,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [
      {
        "_index" : ".ds-logs-my_app-default-2021.06.02-000001",
        "_type" : "_doc",
        "_id" : "-Cqg5HkBfubf8FpwzI3A",
        "_score" : null,
        "fields" : {
          "@timestamp" : [
            "2099-05-08T16:25:42.000Z"
          ]
        },
        "sort" : [
          4081940742000
        ]
      },
      {
        "_index" : ".ds-logs-my_app-default-2021.06.02-000001",
        "_type" : "_doc",
        "_id" : "9yqg5HkBfubf8FpwzI28",
        "_score" : null,
        "fields" : {
          "@timestamp" : [
            "2099-05-07T16:24:32.000Z"
          ]
        },
        "sort" : [
          4081854272000
        ]
      },
      {
        "_index" : ".ds-logs-my_app-default-2021.06.02-000001",
        "_type" : "_doc",
        "_id" : "8yp6zHkBfubf8FpwI40r",
        "_score" : null,
        "fields" : {
          "@timestamp" : [
            "2099-05-06T16:21:15.000Z"
          ]
        },
        "sort" : [
          4081767675000
        ]
      }
    ]
  }
}
```

### 日付範囲での検索
特定の間や IP レンジで検索する場合は、 `range` クエリを使用します。

#### 絶対日時指定
```dockerfile
GET logs-my_app-default/_search
{
  "query": {
    "range": {
      "@timestamp": {
        "gte": "2099-05-05",
        "lt": "2099-05-08"
      }
    }
  },
  "fields": [
    "@timestamp"
  ],
  "_source": false,
  "sort": [
    {
      "@timestamp": "desc"
    }
  ]
}
```

```json
{
  "took" : 18,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 2,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [
      {
        "_index" : ".ds-logs-my_app-default-2021.06.02-000001",
        "_type" : "_doc",
        "_id" : "9yqg5HkBfubf8FpwzI28",
        "_score" : null,
        "fields" : {
          "@timestamp" : [
            "2099-05-07T16:24:32.000Z"
          ]
        },
        "sort" : [
          4081854272000
        ]
      },
      {
        "_index" : ".ds-logs-my_app-default-2021.06.02-000001",
        "_type" : "_doc",
        "_id" : "8yp6zHkBfubf8FpwI40r",
        "_score" : null,
        "fields" : {
          "@timestamp" : [
            "2099-05-06T16:21:15.000Z"
          ]
        },
        "sort" : [
          4081767675000
        ]
      }
    ]
  }
}
```

#### 相対日時指定
日付関数を用いて相対的な時間範囲を定義できます。
下記のクエリで、過去の日付のデータを検索できます。
(投入済のログはヒットしないクエリです。)

```dockerfile
GET logs-my_app-default/_search
{
  "query": {
    "range": {
      "@timestamp": {
        "gte": "now-1d/d",
        "lt": "now/d"
      }
    }
  },
  "fields": [
    "@timestamp"
  ],
  "_source": false,
  "sort": [
    {
      "@timestamp": "desc"
    }
  ]
}
```

```json
{
  "took" : 9,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 0,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [ ]
  }
}
```

### 構造化されていない要素からフィードを抽出
検索時に、ログのメッセージなどのような構造化されていない要素から [動的フィールド (Runtime field)][Define runtime fields in a search request] を抽出することできます。

`event.original` から動的フィールド `source.id` を抽出してみます。
レスポンスに含めるには、 `fields` パラメータにも `source.id` を指定する必要があります。

[Define runtime fields in a search request]: https://www.elastic.co/guide/en/elasticsearch/reference/current/runtime-search-request.html

```dockerfile
GET logs-my_app-default/_search
{
  "runtime_mappings": {
    "source.ip": {
      "type": "ip",
      "script": """
        String sourceip = grok('%{IPORHOST:sourceip} .*')
          .extract(doc["event.original"].value)
          ?.sourceip;
        if (sourceip != null) {
          emit(sourceip);
        }
      """
    }
  },
  "query": {
    "range": {
      "@timestamp": {
        "gte": "2099-05-05",
        "lt": "2099-05-08"
      }
    }
  },
  "fields": [
    "@timestamp",
    "source.ip"
  ],
  "_source": false,
  "sort": [
    {
      "@timestamp": "desc"
    }
  ]
}
```

```json
{
  "took" : 14,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 2,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [
      {
        "_index" : ".ds-logs-my_app-default-2021.06.02-000001",
        "_type" : "_doc",
        "_id" : "9yqg5HkBfubf8FpwzI28",
        "_score" : null,
        "fields" : {
          "@timestamp" : [
            "2099-05-07T16:24:32.000Z"
          ],
          "source.ip" : [
            "192.0.2.242"
          ]
        },
        "sort" : [
          4081854272000
        ]
      },
      {
        "_index" : ".ds-logs-my_app-default-2021.06.02-000001",
        "_type" : "_doc",
        "_id" : "8yp6zHkBfubf8FpwI40r",
        "_score" : null,
        "fields" : {
          "@timestamp" : [
            "2099-05-06T16:21:15.000Z"
          ],
          "source.ip" : [
            "192.0.2.42"
          ]
        },
        "sort" : [
          4081767675000
        ]
      }
    ]
  }
}
```

### クエリの結合
`bool` クエリを用いることで、複数のクエリ結合することができます。
下記のクエリは、 `@timestamp` と動的フィールド`source.ip` の2つの `range` クエリを結合しています。

```dockerfile
GET logs-my_app-default/_search
{
  "runtime_mappings": {
    "source.ip": {
      "type": "ip",
      "script": """
        String sourceip = grok('%{IPORHOST:sourceip} .*')
          .extract(doc[ "event.original" ].value)
          ?.sourceip;
        if (sourceip != null) {
          emit(sourceip);
        }
      """
    }
  },
  "query": {
    "bool": {
      "filter": [
        {
          "range": {
            "@timestamp": {
              "gte": "2099-05-05",
              "lt": "2099-05-08"
            }
          }
        },
        {
          "range": {
            "source.ip": {
              "gte": "192.0.2.0",
              "lte": "192.0.2.240"
            }
          }
        }
      ]
    }
  },
  "fields": [
    "@timestamp",
    "source.ip"
  ],
  "_source": false,
  "sort": [
    {
      "@timestamp": "desc"
    }
  ]
}
```

```json
{
  "took" : 24,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 1,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [
      {
        "_index" : ".ds-logs-my_app-default-2021.06.02-000001",
        "_type" : "_doc",
        "_id" : "8yp6zHkBfubf8FpwI40r",
        "_score" : null,
        "fields" : {
          "@timestamp" : [
            "2099-05-06T16:21:15.000Z"
          ],
          "source.ip" : [
            "192.0.2.42"
          ]
        },
        "sort" : [
          4081767675000
        ]
      }
    ]
  }
}
```

### データの集約
集約を用いることで、データを要約できます。 (メトリクス、統計、その他解析)

下記のクエリでは動的フィールド `http.response.body.bytes` を用いて `average_response_size` を計算し集約しています。
集約は `query` でヒットしたドキュメントに対してのみ計算されます。

```dockerfile {hl_lines=["16-22"]}
GET logs-my_app-default/_search
{
  "runtime_mappings": {
    "http.response.body.bytes": {
      "type": "long",
      "script": """
        String bytes = grok('%{COMMONAPACHELOG}')
          .extract(doc[ "event.original" ].value)
          ?.bytes;
        if (bytes != null) {
          emit(Integer.parseInt(bytes));
        }
      """
    }
  },
  "aggs": {
    "average_response_size":{
      "avg": {
        "field": "http.response.body.bytes"
      }
    }
  },
  "query": {
    "bool": {
      "filter": [
        {
          "range": {
            "@timestamp": {
              "gte": "2099-05-05",
              "lt": "2099-05-08"
            }
          }
        }
      ]
    }
  },
  "fields": [
    "@timestamp",
    "http.response.body.bytes"
  ],
  "_source": false,
  "sort": [
    {
      "@timestamp": "desc"
    }
  ]
}
```

レスポンスの `aggregation` オブジェクトに、集約結果が格納されます。
2件ヒットしそれぞれの `http.response.body.bytes` が 0 と 24736 だったため、集約結果 (平均値) は 12368.0 となりました。

```json {hl_lines=["53-57"]}
{
  "took" : 14,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 2,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [
      {
        "_index" : ".ds-logs-my_app-default-2021.06.02-000001",
        "_type" : "_doc",
        "_id" : "9yqg5HkBfubf8FpwzI28",
        "_score" : null,
        "fields" : {
          "@timestamp" : [
            "2099-05-07T16:24:32.000Z"
          ],
          "http.response.body.bytes" : [
            0
          ]
        },
        "sort" : [
          4081854272000
        ]
      },
      {
        "_index" : ".ds-logs-my_app-default-2021.06.02-000001",
        "_type" : "_doc",
        "_id" : "8yp6zHkBfubf8FpwI40r",
        "_score" : null,
        "fields" : {
          "@timestamp" : [
            "2099-05-06T16:21:15.000Z"
          ],
          "http.response.body.bytes" : [
            24736
          ]
        },
        "sort" : [
          4081767675000
        ]
      }
    ]
  },
  "aggregations" : {
    "average_response_size" : {
      "value" : 12368.0
    }
  }
}
```

### さらなる検索オプション
他の検索オプションなどは、 [共通検索オプション][Common search options] を参照してください。

[Common search options]: https://www.elastic.co/guide/en/elasticsearch/reference/current/search-your-data.html#common-search-options

## Step 5. 後片付け
終わったら、テストデータと index を削除します。

```
DELETE _data_stream/logs-my_app-default
```

### コンテナの停止、削除、ネットワークの削除
1. コンテナの停止
```Shell
docker stop es01-test
docker stop kib01-test
```
2. コンテナとネットワークの削除
```shell
docker network rm elastic
docker rm es01-test
docker rm kib01-test
```

## 他の話題
* データ階層とインデックスライフサイクル管理 (Manage the index lifecycle; ILM) を設定し、時系列データを最大限に活用する
  * [時系列データを Elasticsearch で活用する][Use Elasticsearch for time series data] を参照
* Fleet と Elastic Agent を使って、データソースから直接ログやメトリクスを収集し、 Elasticsearch へ送信する
  * [Fleet クイックスタートガイド][Quick start: Get logs, metrics, and uptime data into the Elastic Stack] を参照 (X-Pack 向け)
* Kibana を使用して、 Elasticsearch のデータの探索、可視化、管理を行う
  * [Kibana クイックスタートガイド][Kibana Quick start] を参照
  
## 次
<!-- TODO: link -->
[Elasticsearch のセットアップ]({{< ref "/elasticsearch/setup/setup.md" >}})
  
[Use Elasticsearch for time series data]: https://www.elastic.co/guide/en/elasticsearch/reference/current/use-elasticsearch-for-time-series-data.html
[Quick start: Get logs, metrics, and uptime data into the Elastic Stack]: https://www.elastic.co/guide/en/fleet/7.13/fleet-quick-start.html
[Kibana Quick start]: https://www.elastic.co/guide/en/kibana/7.13/get-started.html
