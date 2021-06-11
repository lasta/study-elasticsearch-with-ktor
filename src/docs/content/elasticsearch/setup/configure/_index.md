---
title: "構成"
date: 2021-06-10T17:54:27+09:00
draft: false
---

参照元ドキュメント : [Configuring Elasticsearch](https://www.elastic.co/guide/en/elasticsearch/reference/current/settings.html)

Elasticsearch はだいたいデフォルトの設定値で動きます。
[Cluster update settings][Cluster update settings] を使用して起動中のクラスタのほとんどの設定を変更できます。

設定ファイルには、ノード固有の設定 (`node.name` や `path` など) や、ノードがクラスタに参加するための設定 (`cluster.name` や `network.host` など)
を含める必要があります。

## 設定ファイル群の場所
Elasticsearch は3つの設定ファイルを持ちます。

| 設定ファイル名      | 役割                                |
|---------------------|-------------------------------------|
| `elasticsearch.yml` | Elasticsearch の設定                |
| `jvm.options`       | Elasticsearch が起動する JVM の設定 |
| `log4j2.properties` | Elasticsearch のログにまつわる設定  |

これらのファイルは `config` ディレクトリに配置する必要があります。
`config` ディレクトリの場所は、アーカイブ (`tar.gz` や `zip`) を用いてインストールしたか、パッケージ (`deb` や `rpm` など)　インストールしたかによって変わります。

### アーカイブを用いてインストールした場合 (`tar.gz`, `zip`, ......)
`config` ディレクトリのデフォルトの場所は `$ES_HOME/config` になります。
環境変数 `ES_PATH_CONF` を用いて `config` ディレクトリの場所を変更することができます。

```shell
ES_PATH_CONF="/path/to/my/config" "${ES_HOME}/bin/elasticsearch"
```

上記では実行時に `ES_PATH_CONF` を指定しましたが、コマンドラインや shell script などを介して `export` しても構いません。

### パッケージインストールの場合 (`deb`, `rpm`, ......)
`config` ディレクトリのデフォルトの場所は `/etc/elasticsearch` になります。
環境変数 `ES_PATH_CONF` を用いて `config` ディレクトリの場所を変更することができますが、 shell でこれを設定しても不十分です。

代わりに、この変数は `/etc/default/elasticsearch` (Debian 系) および `/etc/sysconfig/elasticsearch` (RHEL 系) を参照します。
`config` ディレクトリの場所を変更するためには、これらのいずれかで `ES_PATH_CONF=/etc/elasticsearch` の行を適宜変更する必要があります。

## 設定ファイルの構成
設定ファイルは YAML 形式です。
例として `data` ディレクトリと `logs` ディレクトリのパスを指定します。

```yaml
path:
    data: /var/lib/elasticsearch
    logs: /var/log/elasticsearch
```

設定は以下のように平坦化しても構いません。

```yaml
path.data: /var/lib/elasticsearch
path.logs: /var/log/elasticsearch
```

YAML では、非スカラ値をシーケンスで表現することができます。

```yaml
discovery.seed_hosts:
   - 192.168.1.10:9300
   - 192.168.1.11
   - seeds.mydomain.com
```

あまり一般的ではありませんが、非スカラ値は配列でも表現できます。

```yaml
discovery.seed_hosts: ["192.168.1.10:9300", "192.168.1.11", "seeds.mydomain.com"]
```

## 環境変数の展開
環境変数は `${...}` 記法を用いることで参照および展開が可能です。

```yaml
node.name: ${HOSTNAME}
network.host: ${ES_NETWORK_HOST}
```

環境変数の値は単純な文字列でなければなりません。
Elasticsearch にリストとして解析させるためには、カンマ区切りの文字列で指定します。

```shell
export HOSTNAME="host1,host2"
```

## クラスタのノードの設定
クラスタやノードの設定はも動的にも静的にも指定可能です。

### 動的
[Cluster update settings API][Cluster update settings] を用いることで、実行中のクラスタに対し、、動的に設定を変更することができます。
`elasticsearch.yml` を使用して、起動していないまたはシャットダウンしたノード上で動的な設定をローカルに構成することもできます。

Cluster update settings API による更新は、永続化されるものと、再起動時にリセットされる一時的なものがあります。
また、 API にて null 値を指定することで、設定をリセットすることができます。

複数の方法で同じ項目を設定した場合、 Elasticsearch は以下の優先度で設定を適用します。

1. 一過性の設定 (transient setting)
2. 永続的な設定 (persistent setting)
3. `elasticsearch.yml` による設定
4. デフォルト値

例えば、一過性の設定を行うことで、永続化した設定や `elasticsearch.yml` による設定を上書きすることができます。

[Cluster update settings]: https://www.elastic.co/guide/en/elasticsearch/reference/current/cluster-update-settings.html

{{< hint info >}}
{{< icon "lightbulb" >}} **Note.**

Cluster update settings API は動的なクラスタ全体の設定を行い、 `elasticsearch.yml` はローカルな設定のみを行うことを推奨します。

Cluster update settings API を使用することで、全てのノードで同じ設定を行う事ができます。
誤って期待していないノードの `elasticsearch.yml` を変更してしまうと、設定の矛盾に気づくのが難しくなります。
{{< /hint >}}

### 静的
静的な設定は起動前またはシャットダウン済のノードに対してのみ `elasticsearch.yml` にて変更可能です。

静的な設定は、クラスタ内のすべてのノードで設定を行う必要があります。
