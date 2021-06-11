---
title: "Docker"
date: 2021-06-10T14:50:03+09:00
draft: false
---

参照元ドキュメント : [Install Elasticsearch with Docker](https://www.elastic.co/guide/en/elasticsearch/reference/current/docker.html)

[centos:8](https://hub.docker.com/_/centos/) ベースの Docker イメージがあります。

イメージは [www.docker.elastic.co](https://www.docker.elastic.co/) で配布されています。
ソースコードは [Github](https://github.com/elastic/elasticsearch/blob/7.13/distribution/docker) にあります。

## Docker image の pull
Elastic Docker レジストリから pull するだけです。

```shell
docker pull docker.elastic.co/elasticsearch/elasticsearch:7.13.1
```

## Docker を用いて単一ノードクラスタで起動する
開発やテストのためにシングルノードの Elasticsearch クラスタを起動するには、 [single-node discovery][single-node discovery] を指定して
[bootstrap checks][bootstrap checks] をスキップします。

```shell
docker run -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:7.13.1
```

[single-node discovery]: https://www.elastic.co/guide/en/elasticsearch/reference/current/bootstrap-checks.html#single-node-discovery
[bootstrap checks]: https://www.elastic.co/guide/en/elasticsearch/reference/current/bootstrap-checks.html

## Docker Compose を利用して複数ノードのクラスタを起動する
Docker Compose を利用して、例として 3-node の Elasticsearch クラスタを起動します。

1. `docker-compose.yml` を作成
{{< expand "docker-compose.yml (click to fold/expand)" >}}
```yaml
version: '2.2'
services:
  es01:
    image: docker.elastic.co/elasticsearch/elasticsearch:7.13.1
    container_name: es01
    environment:
      - node.name=es01
      - cluster.name=es-docker-cluster
      - discovery.seed_hosts=es02,es03
      - cluster.initial_master_nodes=es01,es02,es03
      - bootstrap.memory_lock=true
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
    ulimits:
      memlock:
        soft: -1
        hard: -1
    volumes:
      - data01:/usr/share/elasticsearch/data
    ports:
      - 9200:9200
    networks:
      - elastic
  es02:
    image: docker.elastic.co/elasticsearch/elasticsearch:7.13.1
    container_name: es02
    environment:
      - node.name=es02
      - cluster.name=es-docker-cluster
      - discovery.seed_hosts=es01,es03
      - cluster.initial_master_nodes=es01,es02,es03
      - bootstrap.memory_lock=true
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
    ulimits:
      memlock:
        soft: -1
        hard: -1
    volumes:
      - data02:/usr/share/elasticsearch/data
    networks:
      - elastic
  es03:
    image: docker.elastic.co/elasticsearch/elasticsearch:7.13.1
    container_name: es03
    environment:
      - node.name=es03
      - cluster.name=es-docker-cluster
      - discovery.seed_hosts=es01,es02
      - cluster.initial_master_nodes=es01,es02,es03
      - bootstrap.memory_lock=true
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
    ulimits:
      memlock:
        soft: -1
        hard: -1
    volumes:
      - data03:/usr/share/elasticsearch/data
    networks:
      - elastic

volumes:
  data01:
    driver: local
  data02:
    driver: local
  data03:
    driver: local

networks:
  elastic:
    driver: bridge
```
{{< /expand >}}
    * `es01` は `localhost:9200` を listen し、 `es02` と `es03` は Docker network 上で `es01` とやりとりする
2. `docker-compose` でクラスタを起動する
    ```shell
    docker compose up   
    ```
3. `_cat/nodes` にリクエストを投げて、ノード群が正しく起動していることを確認する
    ```shell
    curl -X GET "localhost:9200/_cat/nodes?v=true&pretty"
    ```
   
ログメッセージはコンソールに送られ、設定された Docker ログドライバによって処理されます。
デフォルトでは、 `docker logs` でログを確認可能です。

Elasticsearch コンテナのログを永続化するようにしたい場合は、環境変数 `ES_LOG_STYLE` に `file` を指定してください。
これにより、 Elasticsearch には他の配布形式と同じログ設定が適用されます。

`docker compose down` を実行することでクラスタを停止できます。
Docker ボリューム内のデータが保持されているため、 `docker compose up` でクラスタを再起動しても、 index 等のデータは維持されています。

クラスタ停止と同時にデータのボリュームを削除するには、 `-v` オプションを指定します。

```shell
docker compose down -v
```

{{< hint info >}}
{{< icon "lightbulb" >}} **Note.**

上記の `docker-compose.yml` では、環境変数 `ES_JAVA_OPTS` にてヒープサイズに 512MB を指定しています。
本番環境では `ES_JAVA_OPTS` でヒープサイズを指定することは非推奨です。

詳細は [Manually set the heap size][Manually set the heap size] を参照してください。

[Manually set the heap size]: https://www.elastic.co/guide/en/elasticsearch/reference/current/docker.html#docker-set-heap-size
{{< /hint >}}

{{< hint warning >}}
{{< icon "comment-dots" >}} **Note.**

この設定では、すべてのネットワークインタフェースで 9200 ポートが公開されてしまうことに注意してください。

Linux 上の iptables を Docker が操作するため、この Elasticsearch は外部に公開されてしまうことを意味し、
またファイアウォールの設定が無視されてしまう可能性があります。

もし 9200 ポートを公開せずにリバースプロキシを利用したい場合は、 `docker-compose.yml` の `9200:9200` を `127.0.0.1:9200:9200` に書き換えてください。
これでホストマシン自身からしか Elasticsearch にアクセスできなくなります。
{{< /hint >}}

{{< hint info >}}
{{< icon "lightbulb" >}} **Note.**

Docker Engine に少なくとも 4GiB 以上のメモリを割り当てておいてください。
{{< /hint >}}

{{< hint info >}}
{{< icon "lightbulb" >}} **Note.**

Docker for Linux には Docker Compose がプリインストールされていません。

[Install Compose on Linux](https://docs.docker.com/compose/install) を確認の上インストールしてください。
{{< /hint >}}

## 複数ノードのクラスタで TLS を有効化する
「[Elasticsearch Docker Container 内の通信を暗号化する][Encrypting communications in an Elasticsearch Docker Container]」と
「[Docker でTSL を有効化して起動する][Run the Elastic Stack in Docker with TLS enabled]」
を参照してください。

[Encrypting communications in an Elasticsearch Docker Container]: https://www.elastic.co/guide/en/elasticsearch/reference/current/configuring-tls-docker.html
[Run the Elastic Stack in Docker with TLS enabled]: https://www.elastic.co/guide/en/elastic-stack-get-started/7.13/get-started-docker.html#get-started-docker-tls

## 本番環境で Docker images を利用する
下記は本番環境で Docker 上で Elasticsearch を運用する際の必須要件と推奨値になります。

* `vm.max_map_count` は少なくとも `262144` 以上に
    * Linux
        * `vm.max_map_count` は `/etc/sysctl.conf` で定義されている
            ```properties
            vm.max_map_count=262144
            ```
        * 稼働中のシステム上で動的に設定するには下記コマンドを実行する
            ```shell
            sysctl -w vm.max_map_count=262144
            ```
    * macOS (Docker for Mac)
        * `vm.max_map_count` は xhyve 仮想マシンで定義されている
            1. 仮想環境のコンソールをアタッチ
                ```shell
                screen ~/Library/Containers/com.docker.docker/Data/vms/0/tty
                ```
            2. `sysctl` で値を設定
                ```shell
                sysctl -w vm.max_map_count=262144
                ```
            3. `Control a d` で `screen` をデタッチ
* `elasticsearch` ユーザが設定ファイル群を読めるようにする
    * デフォルトでは、 `uid:gid` が `1000:0` である `elasticsearch` ユーザがコンテナ内で Elasticsearch を起動する
* [`nofile`][nofile] と [`nproc`][nproc] の `ulimit` を増やす
    * Docker daemon の [init system](https://github.com/moby/moby/tree/ea4d1243953e6b652082305a9c3cda8656edab26/contrib/init) が許容範囲内の値に設定されていることを確認する
        * デフォルト値の確認
            ```shell
            docker run --rm centos:8 /bin/bash -c 'ulimit -Hn && ulimit -Sn && ulimit -Hu && ulimit -Su'
            ```
        * コンテナ単位で指定する場合は、 `docker run` 時に下記のオプションを指定する
            ```shell
            --ulimit nofile=65535:65535
            ```
* swap を無効化する
    * `bootstrap.memory_lock: true` を指定した場合、 Docker Daemon で `memolock: true` ulimit を指定するか、前述のサンプルにように docker compose ファイルで明示的に定義する必要がある
    * `docker run` を使用する場合は、下記のオプションを指定する
        ```shell
        -e "bootstrap.memory_lock=true" --ulimit memlock=-1:-1
        ```
* 公開ポートをランダム化する
    * イメージでは TCP ポート 9200 および 9300 が公開される
    * ホストごとに1つのコンテナを固定する場合を除き、 `--pulish-all` で公開ポートをランダム化する
* ヒープサイズを設定する
    * デフォルトでは、ノードの役割とコンテナが利用可能なメモリサイズをもとに、 JVM のヒープサイズを自動的に決定される
        * ほとんどの実働環境では、このデフォルト値を利用することを推奨する
    * 手動で設定する場合は、 [JVM options][JVM options] ファイルを `/usr/share/elasticsearch/config/jvm.options.d` に配置されるようにする
    * `$ES_JAVA_OPTS` 環境変数は他のすべての JVM オプションよりも優先されてしまうため、テスト環境以外での利用は非推奨
* image のバージョンを固定する
    * 例: `docker.elastic.co/elasticsearch/elasticsearch:7.13.1`
* `/usr/share/elasticsearch/data` にデータボリュームをマウントする
    * コンテナが死んでもデータを失わないようにするため
    * Elasticsearch では I/O 速度が重要になるが、 Docker storage drive は I/O の最適化がされていないため
    * [Docker volume plugins](https://docs.docker.com/engine/extend/plugins/#volume-plugins) が利用可能になるため
* devicemapper ストレージドライバを利用している場合、デフォルトの `loop-lvm` モードではなく `direct-lvm` モードを利用するよう docker-engine を設定する
* ログを一元化する
    * 別のログドライバを使用してログを一元化する
    * デフォルトの `json-file` ログドライバは、本番環境での利用に不適

[nofile]: https://www.elastic.co/guide/en/elasticsearch/reference/current/setting-system-settings.html
[nproc]: https://www.elastic.co/guide/en/elasticsearch/reference/current/max-number-threads-check.html
[JVM options]: https://www.elastic.co/guide/en/elasticsearch/reference/current/advanced-configuration.html#set-jvm-options

## 次のステップ
Elasticsearch のテスト環境が整いました。
一方で、本格的な開発や本番環境を構築する前に、いくつか追加の設定が必要になります。

* [Elasticsearch の設定を学ぶ][configure Elasticsearch]
* [重要な Elasticsearch の設定][important Elasticsearch settings]
* [重要なシステムの設定][important system settings]

[configure Elasticsearch]: {{< relref "../configure/_index.md" >}}
[important Elasticsearch settings]: {{< relref "../configure/important.md" >}}
[important system settings]: https://www.elastic.co/guide/en/elasticsearch/reference/current/system-config.html
