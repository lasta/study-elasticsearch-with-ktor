---
title: "本番運用時の重要な設定"
date: 2021-06-11T12:16:00+09:00
draft: false
---

参照元ドキュメント : [Important Elasticsearch configuration](https://www.elastic.co/guide/en/elasticsearch/reference/current/important-settings.html)

ローカル等で使い始める分にはほとんどデフォルトの設定で問題ありません。
クラスタを本番運用するためには考慮すべき設定値を解説します。

Elastic Cloud を利用する場合は特に考慮不要です。

{{< toc >}}

## パスの設定
Elasticsearch は index したデータとデータストリームを `data` ディレクトリ内に書き込みます。
クラスタの状態等のログは `logs` ディレクトリに書き込みます。

Mac や Linux 上に [`tar.gz` を用いてインストール][targz] した場合、および Windows 上に [`.zip` を用いてインストール][winzip] した場合は、
`data` ディレクトリと `logs` ディレクトリは `$ES_HOME` 配下に配置されます。
ですが、この `$ES_HOME` ディレクトリは Elaticsearch のアップグレード時に削除される危険性があります。

本番環境では、 `elasticsearch.yml` にて `$ES_HOME` 外に `path.data` と `path.logs` を設定することを強く推奨します。

[Docker][docker] 、 [RPM][rpm] 、 [MacOS Homebrew][homebrew] 、 [Windows `.msi`][msi] でインストールした場合は、
`logs` と `data` ディレクトリはデフォルトで `$ES_HOME` 外の場所に設定されています。

[targz]: {{< relref "../install/targz.md" >}}
[winzip]: https://www.elastic.co/guide/en/elasticsearch/reference/current/zip-windows.html
[docker]: {{< relref "../install/docker.md" >}}
[debian]: https://www.elastic.co/guide/en/elasticsearch/reference/current/deb.html
[rpm]: {{< relref "../install/rpm.md" >}}
[homebrew]: https://www.elastic.co/guide/en/elasticsearch/reference/current/brew.html
[msi]: https://www.elastic.co/guide/en/elasticsearch/reference/current/windows.html

{{< hint danger >}}
{{< icon "flag" >}} **重要**

エラーを避けるために、 `path.data` ディレクトリ配下を参照するのは Elasticsearch だけにしてください。

ウィルス対策ソフトやバックアップシステムなど、ファイルを開いたりロックを取るようなサービスでは、 `path.data` ディレクトリを走査対象外に指定してください。

{{< /hint >}}

Linux 系および MacOS での設定例です。

```yaml
path:
  data: /var/data/elasticsearch
  logs: /var/log/elasticsearch
```

## `path.data` の複数指定

{{< hint warning >}}
{{< icon "comment-dots" >}} **注意**

Elasticsearch 7.13.0 にて非推奨になりました。

{{< /hint >}}

`path.data` に複数のパスを指定することができます。
Elasticsearch は指定されたすべてのパスにノードのデータを保存しますが、各シャードのデータは同じパスに保存します。

Elasticsearch はノードのデータのパス間で shard のバランシングをしません。
ある1つのパスでディスク使用量が多いと、「ノード全体のディスク使用量が多い」ステータス ([high disk usage watermark][Disk-based allocation settings]) になります。
この状態になると、他のパスのディスク容量に余裕があったとしても、そのノーシドに shard を追加できなくなります。
追加のディスク容量が必要な場合は、データのパスを追加するのではなく、ノードを追加することを推奨します。

[Disk-based shard allocation settings]: https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-cluster.html#disk-based-shard-allocation

## クラスタ名
同一クラスタ内のすべてのノードの `cluster.name` を揃える必要があります。
デフォルトの名前は `elasticsearch` ですが、クラスタの目的を説明する適切な名前に変更してください。

```yaml
cluster.name: logging-prod
```

{{< hint danger >}}
{{< icon "flag" >}} **重要**

異なる環境でクラスタ名を再利用しないでください。
ノードが想定外のクラスタに属してしまうかもしれません。
{{< /hint >}}

## ノード名
`node.name` に人間が理解できる識別子を指定します。

この名前は、多くの API のレスポンスに含まれます。
ノード名のデフォルト値は Elasticsearch 起動時のマシンのホスト名ですが、 `elasticsearch.yml` で明示的に設定することもできます。

```yaml
node.name: prod-data-2
```

## ネットワークホスト
デフォルトでは、 `127.0.0.1` や `[::1]` などのループバックアドレスのみバインドします。
開発やテストの際に1台のサーバで複数のノードからなるクラスタを構築する場合は問題ないですが、本番環境ではサーバは複数台構成にになります。
ネットワークの設定項目はたくさんありますが、多くの場合設定が必要となるのは `network.host` だけになります。

```yaml
network.host: 192.168.1.10
```

{{< hint warning >}}
{{< icon "comment-dots" >}} **注意**

`network.host` に値を指定すると、 Elasticsearch は開発モードから本番モードへ移行しているとみなし、
システム起動時の殆どのチェック項目について失敗した際の警告レベルを warning から exception に昇格させます。

詳細は [開発モードと本番モードの違い][development and production mode] を確認してください。

[development and production mode]: https://www.elastic.co/guide/en/elasticsearch/reference/current/system-config.html#dev-vs-prod
{{< /hint >}}

## ディスカバリとクラスタ形成
本番環境に導入する前に、ディスカバリとクラスタ形成の設定を行ってください。

クラスタ内のノードが互いを発見できるようになり、マスターノードを選出できるようになります。

### `discovry.seed_hosts`
ネットワークの設定を特に行わなくても、ローカルの `9300` 〜 `9305` 番ポートをスキャンして、同じサーバ上で起動している他のノードに接続します。
この動作のおかげで、、特に設定をしなくても自動的にクラスタが作成されます。

他のホスト上のノードとクラスタを形成したい場合は、静的に `discovery.seed_hosts` を設定します。
この設定により Master 候補 (master eligible node) であり生きていてコンタクト可能な、クラスタ内の他のノードの一覧を [ディスバリプロセス][discovery process] のシードに提供されます。
クラスタ内にあるすべての Master 候補のノードのアドレスを、配列で定義します。
各アドレスは、最終的に DNS 等で解決できれば、ホスト名でも IP アドレスの直指定でも問題ありません。

[discovery process]: https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-discovery-hosts-providers.html

```yaml
discovery.seed_hosts:
  - 192.168.1.10:9300
  - 192.168.11.1  # 1.
  - seeds.mydomain.com  # 2.
  - [0:0:0:0:0:ffff:c0a8:10c]:9301  # 3.
```

{{< hint ok >}}
{{< icon "sticky-note" >}} **1.**

ポート番号が指定されなかった場合は、デフォルト値の `9300` ポートになります。 ([オーバーライド可能](https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-discovery-hosts-providers.html#built-in-hosts-providers))
{{< /hint >}}

{{< hint ok >}}
{{< icon "sticky-note" >}} **2.**

ホスト名が複数の IP アドレスに解決された場合、解決されたすべての IP アドレスに有るノードをディスカバリしようとします。
{{< /hint >}}

{{< hint ok >}}
{{< icon "sticky-note" >}} **3.**

IPv6 の場合、角括弧で囲まなければなりません
{{< /hint >}}

Master 候補のノードが固定の名前やアドレスを持っていない場合は、 [代替ホストプロバイダ][alternative hosts provider] を使用してアドレスを動的に検索できるようにします。

[alternative hosts provider]: https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-discovery-hosts-providers.html#built-in-hosts-providers

### `cluster.initial_master_nodes`
初めてクラスタを起動する際、 [cluster bootstrapping] ステップでは、最初の Master 選出選挙の候補となるノード群を決定します。
[開発モード][development mode] ではディスカバリ設定が構成されないため、ノード自身によって自動的に実行されます。

自動的な bootstrap は [本質的には安全ではない][inherently unsafe] ため、本番モードで新しいクラスタを起動する場合は、 Master 候補を
`cluster.initial_master_nodes` に明示的に指定します。

[cluter bootstrapping]: https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-discovery-bootstrap-cluster.html
[development mode]: https://www.elastic.co/guide/en/elasticsearch/reference/current/bootstrap-checks.html#dev-vs-prod-mode
[inherently unsafe]: https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-discovery-quorums.html

{{< hint danger >}}
{{< icon "flag" >}} **重要**

初回のクラスタ構築に成功したあとは、各ノードから `cluster.initial_master_nodes` の設定を削除してください。
クラスタを再起動する際や新しいノードを追加する際には指定しないでください。
{{< /hint >}}

```yaml
discovery.seed_hosts:
   - 192.168.1.10:9300
   - 192.168.1.11
   - seeds.mydomain.com
   - [0:0:0:0:0:ffff:c0a8:10c]:9301
cluster.initial_master_nodes: 
   - master-node-a
   - master-node-b
   - master-node-c
```

`cluster.initial_master_nodes` には Master 候補のノード名を指定します。
各ノードの `node.name` で指定した値を正確に指定してください。
デフォルトではホスト名になっています。

たとえば、ノード名に `master-node-a.example.com` のような FQDN (Fully-qualified domain name; 完全修飾ドメイン名) を使用している場合は、
その FQDN を指定しなければなりません。
逆に、 `node.name` が末尾に修飾子を持たない生のホスト名の場合は、 `cluster.initial_master_nodes` では末尾の修飾子を省略しなければなりません。

## ヒープサイズの設定
Elasticsearch は、ノードの[役割][roles]とサーバのメモリ量に基づいて、 JVM のヒープサイズを自動的に設定します。
ほとんどの場合では、デフォルト状態のままにすることを推奨します。

[roles]: https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-node.html#node-roles

{{< hint info >}}
{{< icon "lightbulb" >}} **Note**

ヒープサイズの自動調整は、バンドルされている  JDK 、または Java 14 以降の JRE を利用している場合のみ有効になります。
{{< /hint >}}

別途調整が必要な場合は、手動で指定することができます。
[settings the JVM heap size](https://www.elastic.co/guide/en/elasticsearch/reference/current/advanced-configuration.html#set-jvm-heap-size) を参照してください。

## JVM のヒープダンプのパス
デフォルトでは、 OOME が発生した際にヒープダンプを作成します。
RPM および Debian 系では `/var/lib/elasticsaerch` 、 zip や tar.gz でインストールした場合は、 `ES_ROOT` に配置されます。

パスを変更したい場合は、 `jvm.options` にある `-XX:HeapDumpPath=...` を修正してください。

* ディレクトリを指定した場合
    * ヒープダンプファイル名は PID に基づいて自動生成される
* ファイル名を指定した場合
    * 指定したパスにファイルが存在しない場合、そのパスにヒープダンプが生成される
    * 指定したパスにファイルが存在する場合、ヒープダンプに失敗する

## GC ログの設定
GC ログの出力はデフォルトで有効になっています。
[`jvm.options`][jvm.options] で設定可能です。
ログのデフォルトの出力先は Elasticsearch のログと同じ場所になります。
ログローテーションは 64MB ごとに行われ、合計で最大 2GB 消費します。

[JEP 158: Unified JVM Logging][JEP 158] にあるコマンドラインオプションを用いて、ログ設定を変更することができます。
デフォルトの `jvm.options` ファイルを直接編集しない限り、自分で設定したものに Elasticsearch のデフォルトの設定が追加されます。
デフォルトの設定を無効化するには、 `-Xlog:disable` でログを無効にし、その上で独自のコマンドラインオプションを指定します。
この設定により全ての JVM ログが無効になるので、利用可能なオプションを確認の上、必要なものはすべて有効化してください。

JEP に含まれていないその他のオプションについては、 [Enable Logging with the JVM Unified Logging Framework][Enable Logging with the JVM Unified Logging Framework] を参照してください。

[jvm.options]: https://www.elastic.co/guide/en/elasticsearch/reference/current/advanced-configuration.html#set-jvm-options
[JEP 158]: https://openjdk.java.net/jeps/158
[Enable Logging with the JVM Unified Logging Framework]:https://docs.oracle.com/en/java/javase/13/docs/specs/man/java.html#enable-logging-with-the-jvm-unified-logging-framework

## 例
* GC ログの出力先を `/opt/my-app/gc.log`
* 他、いくつかサンプルでオプションを指定
* `$ES_HOME/config/jvm.options.d/gc.options` に作成

```
# デフォルトの設定をすべて無効化
-Xlog:disable

# `uptime` の代わりに `utctime` を指定している以外、 JEP 158 のデフォルトの設定のとおり
-Xlog:all=warning:stderr:utctime,level,tags

# GC ログの出力先の変更、他
-Xlog:gc*,gc+age=trace,safepoint:file=/opt/my-app/gc.log:utctime,pid,tags:filecount=32,filesize=64m
```

Docker 版 ES にて GC のデバッグログを標準エラー (`stderr`) に変更する例です。
コンテナのオーケストレーターにその後のログの処理を移譲します。
環境変数 `ES_JAVA_OPTS` に指定します。

```shell
MY_OPTS="-Xlog:disable -Xlog:all=warning:stderr:utctime,level,tags -Xlog:gc=debug:stderr:utctime"
docker run -e ES_JAVA_OPTS="$MY_OPTS" # etc
```

## tmp ファイルの設定
デフォルトでは、 Elasticsearch の起動スクリプトがシステムの tmp ディレクトリ直下に専用のtmp ファイル用のディレクリトリを生成します。

一部の Linux ディストリビューションは、最近アクセスされていない tmp ファイルを、システムのユーティリティが `/tmp` からファイルやディレクトリを削除します。
Elasticsearch による tmp ディレクトリを必要とする機能が長時間使用されなかった場合、 Elasticsearch の実行中に tmp ディレクトリが削除されてしまう可能性があり、
その結果問題が発生する場合があります。

`.deb` または `.rpm` パッケージを用いて Elasticsearch をインストールし `systemd` で実行する場合、 Elasticsearch が使用する tmp ディレクトリは
定期的な `/tmp` 配下の削除の対象から除外されます。

Linux や MacOS に `tar.gz` 形式のものをインストールし長期間運用する場合は、 Elasticsearch 専用の tmp ディレクトリを作成することを検討してください。
このディレクトリは Elasticsearch を起動するユーザのみがアクセス可能になるようパーミッションを設定する必要があります。
そして、 Elasticsearch を起動する前に、環境変数 `$ES_TMPDIR` にそのパスを指定してください。

## JVM fatal error ログ
Fatal error log のログ出力先のデフォルトは `path.logs` になります。
RPM や Debian 系では `/var/log/elasticsearch` 、 Linux や MacOS 、 Windows の場合は `$ES_ROOT/logs` になります。

このログは、 JVM にて segmentation fault などの致命的なエラーが発生した場合に生成されます。
`jvm.options` の `-XX:ErrorFile=...` で変更可能です。

## クラスタのバックアップ
[Snapshots][snapshots] を作成することで、データの損失を防ぐことができます。
[Snapshot lifecycle management][Snapshot lifecycle management] を用いることで、かんたんに定期的にクラスタの snapshot を作成できます。

詳細は [Back up a cluster][Back up a cluster] を参照してください。

[snapshots]: https://www.elastic.co/guide/en/elasticsearch/reference/current/snapshot-restore.html
[Snapshot lifecycle management]: https://www.elastic.co/guide/en/elasticsearch/reference/current/snapshot-lifecycle-management.html
[Back up a cluster]: https://www.elastic.co/guide/en/elasticsearch/reference/current/backup-cluster.html

{{< hint warning >}}
{{< icon "comment-dots" >}} **注意**

**Snapshot の作成以外の方法でバックアップを取ることはできません。**

ノードの `path.data` ディレクトリをコピーして Elasticsearch クラスタのバックアップを取ることはできません。
ファイルシステムレベルのバックアップからデータを復元する方法はサポートされていません。
このようなバックアップからクラスタをリストアしようとすると、ファイルの破損や欠落、その他データの不整合が発生してクラスタの起動に失敗したり、
データの一部が静かに失われた上でクラスタの起動に成功したように見える可能性があります。
{{< /hint >}}
