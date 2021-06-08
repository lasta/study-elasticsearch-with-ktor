---
title: "tar.gz (Linux, MacOS)"
date: 2021-06-08T12:19:10+09:00
draft: true
---

Linux および MacOS 向けの `tar.gz` アーカイブを用いたインストール手順

最新安定版の Elasticsearch は [Download Elasticsearch](https://www.elastic.co/downloads/elasticsearch) にあります。
過去のバージョンは [Past Releases page](https://www.elastic.co/downloads/past-releases) にあります。

{{< hint info >}}
Elasticsearch は OpenJDK がバンドルされています。 (GPLv2+CE)

自前の JVM を利用する場合は、 [こちら]({{< relref "../_index.md#java-jvm-バージョン" >}}) を確認してください。
{{< /hint >}}

## for Linux
Elasticsearch v7.13.1 の例です。

```shell
wget https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-7.13.1-linux-x86_64.tar.gz
wget https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-7.13.1-linux-x86_64.tar.gz.sha512
shasum -a 512 -c elasticsearch-7.13.1-linux-x86_64.tar.gz.sha512 # 1.
tar -xzf elasticsearch-7.13.1-linux-x86_64.tar.gz
cd elasticsearch-7.13.1/ # 2.
```

1. `elasticsearch-{version}-linux-x86_64.tar.gz: OK` と出力されれば ok
2. このディレクトリが `$ES_HOME` となる

## for MacOS
Elasticsearch v7.13.1 の例です。

```shell
wget https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-7.13.1-darwin-x86_64.tar.gz
wget https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-7.13.1-darwin-x86_64.tar.gz.sha512
shasum -a 512 -c elasticsearch-7.13.1-darwin-x86_64.tar.gz.sha512 
tar -xzf elasticsearch-7.13.1-darwin-x86_64.tar.gz
cd elasticsearch-7.13.1/ 
```

1. `elasticsearch-{version}-darwin-x86_64.tar.gz: OK` と出力されれば ok
2. このディレクトリが `$ES_HOME` となる

## CLI から Elasitcsearch を起動
```shell
./bin/elasticsearch
```

{{< hint info >}}
Elasticsearch に同梱されているスクリプトは、配列をサポートするバージョンの bash (4系以降) を必要とし、 /bin/bash で利用できることを前提としています。
{{< /hint >}}

## Elasticsearch が起動していることの確認
`localhost:9200` に HTTP リクエストを送り、期待通りに返却されることを確認します。

```shell
curl -X GET "localhost:9200/?pretty"
```

Docker 版の例ですが、下記と似たようなレスポンスが返却されれば ok です。

```json
{
  "name" : "d1c7ea7b50cf",
  "cluster_name" : "docker-cluster",
  "cluster_uuid" : "Udx0AkrqR8i-TeUcxAQtKQ",
  "version" : {
    "number" : "7.13.1",
    "build_flavor" : "default",
    "build_type" : "docker",
    "build_hash" : "9a7758028e4ea59bcab41c12004603c5a7dd84a9",
    "build_date" : "2021-05-28T17:40:59.346932922Z",
    "build_snapshot" : false,
    "lucene_version" : "8.8.2",
    "minimum_wire_compatibility_version" : "6.8.0",
    "minimum_index_compatibility_version" : "6.0.0-beta1"
  },
  "tagline" : "You Know, for Search"
}
```

`-q` または `--quiet` をコマンドラインオプションに指定することで、標準出力 (`stdout`) へのログ出力を無効化できます。

## デーモン起動

Elasticsearch をデーモンとして起動する場合は、 `-d` オプションをつけます。
`-p` オプションでプロセスIDをファイルに記録できます。

ログファイルは `$ES_HOME/logs/` 配下に配置されます。
Elasticsearch を停止するには、 `pid` ファイルに記録されているプロセスIDを指定します。

```shell
pkill -F pid
```

{{< hint info >}}
`.tar.gz` パッケージ版の Elasticsearch には `systemd` モジュールが含まれていません。
Elasticsearch をサービスとして管理する場合は、代わりに Debian または RPM パッケージを利用してください。
{{< /hint >}}

## コマンドラインから Elasticsearch を設定する
Elasticsearch はデフォルトで `$ES_HOME/config/elasticsearch.yml` に記載されている設定を読み込みます。
設定ファイルのフォーマットは [Configuring Elasticsearch][Configuring Elasticsearch] に説明があります。

設定ファイルで指定できる設定は、 `-E` オプションを用いることでコマンドラインでも指定できます。

```shell
./bin/elasticsearch -d -Ecluster.name=my_cluster -Enode.name=node_1
```

[Configuring Elasticsearch]: https://www.elastic.co/guide/en/elasticsearch/reference/current/settings.html

{{< hint info >}}
通常、クラスタ全体の設定 (`cluster.name` など) は `elasticsearch.yml` で定義します。
ノード固有の設定 (`node.name` など) はコマンドラインで指定します。
{{< /hint >}}
