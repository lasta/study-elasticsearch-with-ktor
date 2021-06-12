---
title: "rpm"
date: 2021-06-08T17:08:51+09:00
draft: false
---

参照元ドキュメント : [Install Elasticsearch with RPM](https://www.elastic.co/guide/en/elasticsearch/reference/current/rpm.html)

RPM 版の Elasticsearch は [ウェブサイト上から直接ダウンロード][downloaded from our website] するか、 [RPM リポジトリから直接取得][rpm repository] できます。
OpenSuSE、SLES、CentOS、Red Hat、Oracle Enterprise といった RPM ベースのシステムにインストールすることができます。

{{< hint info >}}
{{< icon "lightbulb" >}} **Note.**

RPM インストールは、 CentOS 5、SLES 11 を始めとした旧バージョンの RPM へのインストールをサポートしていません。
代わりに [tar.gz でのインストール][targz] を検討してください。

[targz]: {{< relref "targz.md" >}}
{{< /hint >}}

最新の安定版の Elasticsearch は [Download Elasticsearch][Download Elasticsearch] にあります。
旧バージョンは [こちら][Past Releases page] にあります。

[downloaded from our website]: https://www.elastic.co/guide/en/elasticsearch/reference/current/rpm.html#install-rpm
[rpm repository]: https://www.elastic.co/guide/en/elasticsearch/reference/current/rpm.html#rpm-repo
[Download Elasticsearch]: https://www.elastic.co/downloads/elasticsearch
[Past Releases page]: https://www.elastic.co/downloads/past-releases

## GPG Key をインポート
すべてのパッケージに Elasticsearch Signing Key を用いて fingerprint で署名しています。
(PGP key [D88E42B4](https://pgp.mit.edu/pks/lookup?op=vindex&search=0xD27D666CD88E42B4), [https://pgp.mit.edu/](https://pgp.mit.edu/) から利用可能)

```text
4609 5ACC 8548 582C 1A26 99A9 D27D 666C D88E 42B4
```

公開鍵のダウンロードは下記コマンドで行います。

```shell
rpm --import https://artifacts.elastic.co/GPG-KEY-elasticsearch
```

## RPM リポジトリからインストール
下記の内容で `elasticsearch.repo` ファイルを作成します。
RHEL 系では `/etc/yum.repos.d/` 配下に、 OpenSuSE 系では `/etc/zypp/repos.d/` 配下に作成します。

```
[elasticsearch]
name=Elasticsearch repository for 7.x packages
baseurl=https://artifacts.elastic.co/packages/7.x/yum
gpgcheck=1
gpgkey=https://artifacts.elastic.co/GPG-KEY-elasticsearch
enabled=0
autorefresh=1
type=rpm-md
```

リポジトリ利用の準備が整ったので下記コマンドでインストールします。

* CentOS または古い RHEL 系
    ```shell
    sudo yum install --enablerepo=elasticsearch elasticsearch
    ```
* Fedora または新しい RHEL 系
    ```shell
    sudo dnf install --enablerepo=elasticsearch elasticsearch
    ```
* OpenSUSE 系
    ```shell
    sudo zypper modifyrepo --enable elasticsearch && \
      sudo zypper install elasticsearch; \
      sudo zypper modifyrepo --disable elasticsearch
    ```

{{< hint info >}}
{{< icon "lightbulb" >}} **Note.**

設定したリポジトリは、デフォルトでは無効になっています。
システムの他の部分をアップグレードした際に Elasticsearch も同時にアップグレードされてしまうことを防ぐためです。

インストールやアップグレード時は、上記のように明示的にリポジトリを有効化することを推奨します。
{{< /hint >}}

## 手動で RPM をインストール
下記コマンドで Elasticsearch 7.13.1 をダウンロードして手動でインストールできます。

```shell
wget https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-7.13.1-x86_64.rpm
wget https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-7.13.1-x86_64.rpm.sha512
shasum -a 512 -c elasticsearch-7.13.1-x86_64.rpm.sha512  ## 1.
sudo rpm --install elasticsearch-7.13.1-x86_64.rpm
```

1. 公開している checksum とダウンロードされた RPM の SHA ハッシュを比較している
    * `elasticsearch-{version}-x86_64.rpm: OK` と返却されれば OK

{{< hint info >}}
{{< icon "lightbulb" >}} **Note.**

`systemd` ベースのディストリビューションでは、インストールスクリプトがカーネルのパラメータ (`vm.max_map_count` など) を設定しようとします。
`systemd-sysctl.service` ユニットをマスクすることで、これを回避することができます。
{{< /hint >}}

## SysV `init` v.s. `systemd`
Elasticsearch をインストールしても、起動時に自動起動するようにはなっていません。
自動起動は、使っているシステムが SysV `init` を使っているか `systemd` (新しめのディストリビューション) を使っているかで異なります。

下記コマンドで確認できます。

```shell
ps -p 1
```

### SysV `init` を用いて Elasticsearch を起動する
システム起動時に Elasticsearch を自動的に起動するには `chkconfig` コマンドを利用します。

```shell
sudo chkconfig --add elasticsearch
```

Elasticsearch の起動と停止は下記コマンドでできます。

```shell
sudo -i service elasticsearch start
sudo -i service elasticsearch stop
```

なんからの理由で Elasticsearch の起動に失敗した場合、標準出力に理由を出力します。
ログファイルは `/var/log/elasticsearch/` にあります。

### `systemd` を用いて Elasticsearch を起動する
システム起動時に Elasticsearch を自動的に起動するには `chkconfig` コマンドを利用します。

```shell
sudo /bin/systemctl daemon-reload
sudo /bin/systemctl enable elasticsearch.service
```

Elasticsearch の起動と停止は下記コマンドでできます。

```shell
sudo systemctl start elasticsearch.service
sudo systemctl stop elasticsearch.service
```

上記のコマンドは、 Elasticsearch が正常に起動したかどうかのフィードバックをしません。
ログは `/var/log/elasticsearch/` を確認してください。

Elasticsearch のキーストアをパスワードで保護している場合は、ローカルファイルと `systemd` の環境変数を使って、 `systemd` が
キーストアのパスワードを読めるようにする必要があります。

このローカルファイルが存在する場合は安全に保護しておき、 Elasticsearch が正常に起動したらそのファイルを削除してください。

```shell
echo "keystore_password" > /path/to/my_pwd_file.tmp
chmod 600 /path/to/my_pwd_file.tmp
sudo systemctl set-environment ES_KEYSTORE_PASSPHRASE_FILE=/path/to/my_pwd_file.tmp
sudo systemctl start elasticsearch.service
```

デフォルトの設定では、 Elasticsearch サービスは `systemd` ジャーナルに情報をログ出力しません。
`journalctl` ログを有効にするためには、 `elasticsearch.service` ファイルの `ExecStart` コマンドラインから `--quiet` オプションを削除してください。

`systemd` のログが有効になると、 `journalctl` コマンドでログを得られるようになります。

journal を tail するコマンドは下記です。

```shell
sudo journalctl -f
```

elasticsearch サービスの journal エントリを一覧化するコマンドは下記です。

```shell
sudo journalctl --unit elasticsearch
```

特定の日付時刻以降の elasticsearch サービスの jounal エントリを確認するコマンドは下記です。

```shell
sudo journalctl --unit elasticsearch --since  "2016-10-30 18:17:16"
```

他のオプションは `man journalctl` か [https://www.freedesktop.org/software/systemd/man/journalctl.html](https://www.freedesktop.org/software/systemd/man/journalctl.html)
を確認してください。

## Elasticsearch の起動の確認
`localhost` の `9200` 番ポートに HTTP リクエストを送ることで、 Elasticsearch ノードが起動しているか確認できます。

```shell
curl -X GET "localhost:9200/?pretty"
```

下記のようなレスポンスが返却されれば OK です。

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

## Elasticsearch の設定
`/etc/elasticsearch` ディレクトリに Elasticsearch のランタイムの設定があります。
パッケージインストールでは、このディレクトリとその中のファイル群の所有権は `root:elasticsearch` に設定されます。

`setgid` フラグは `/etc/elasticsearch` ディレクトリにグループパーミッションを適用し、 Elasticsearch が含まれるすべてのファイルと
サブディレクトリを読めるようにします。
すべてのファイルとサブディレクトリは `root:elasticsearch` の所有権を継承します。
[elasticsearch-keystore tool][elasticsearch-keystore tool] などこのディレクトリ配下からコマンドを実行するには、
`root:elasticsearch` のパーミッションが必要です。

Elasticsearch はデフォルトでは `/etc/elasticsearch/elasticsaerch.yml` ファイルから設定を読み込みます。
この設定ファイルの書式は [Configuring Elasticsearch][Configure Elasticsearch] を確認してください。

RPM 版には `/etc/sysconfig/elasticsearch` にシステムの設定ファイルがあります。
このファイルでは、下記のパラメータを設定できます。

| 環境変数             | 説明                                                                                   | デフォルト値        |
|----------------------|----------------------------------------------------------------------------------------|---------------------|
| `ES_JAVA_HOME`       | 自前でインストールした Java へのパス (デフォルトでは内包している OpenJDK が利用される) | (なし)              |
| `MAX_OPTION_FILES`   | ファイルを開ける最大の数                                                               | 65535               |
| `MAX_LOCKED_MEMORY`  | lock (メモリの内容が swap 領域に吐き出されないようにする; `mlock`) できる最大容量      |                     |
| `MAX_MAP_COUNT`      | プロセスが持つことのできるメモリマップ領域の最大数 (※1)                                | 2621144             |
| `ES_PATH_CONF`       | 設定ファイル群のディレクトリのパス (※2)                                                | `/etc/elasticsearch |
| `ES_JAVA_OPTS`       | 追加したい JVM システムプロパティ                                                      |                     |
| `RESTART_ON_UPGRADE` | パッケージアップグレードの際に再起動するか (※3)                                            | `false`             |

{{< hint ok >}}
{{< icon "sticky-note" >}} **※1** `MAX_MAP_COUNT`

インデックスストアタイプに `mmapfs` を使用している場合は、大きい値を設定してください。

詳しくは [linux kernel documentation](https://github.com/torvalds/linux/blob/master/Documentation/sysctl/vm.txt) の `max_map_count` を参照してください。
{{< /hint >}}


{{< hint ok >}}
{{< icon "sticky-note" >}} **※2** `ES_PATH_CONF`

下記3ファイルが必ず含まれている必要があります。

* `elasticsearch.yml`
* `jvm.options`
* `log4j2.properties`

{{< /hint >}}

{{< hint ok >}}
{{< icon "sticky-note" >}} **※3** `RESTART_ON_UPGRADE`

`false` の場合、パッケージを手動でインストールしたあとに、 Elasticsearch インスタンスを再起動する必要があります。

クラスタ内でのアップグレードにより、シャードの再配置が継続的に行われ、ネットワークトラフィックが増大し、クラスタの応答が悪くなることを防ぐことを目的とした設定です。
{{< /hint >}}

{{< hint info >}}
{{< icon "lightbulb" >}} **Note.**

`systemd` を利用しているディストリビューションでは、 `/etc/sysconfig/elasticsearch` ファイルではなく `systemd` 経由でシステムのリソース利用の制限を設定する必要があります。

詳しくは [Systemd configuration][Systemd configuration] を参照してください。

[Systemd configuration]: https://www.elastic.co/guide/en/elasticsearch/reference/current/setting-system-settings.html#systemd
{{< /hint >}}

<!-- TODO: update link to `elasticsearch-keystore tool` -->

[elasticsearch-keystore tool]: https://www.elastic.co/guide/en/elasticsearch/reference/current/secure-settings.html

## RPM でのディレクトリ構成
RPM は設定ファイル、ログ、データを RPM ベースのシステムにおける適切な場所に配置します。

| 種別        | 説明                                                                                                                      | デフォルト値                       | 設定           |
|-------------|---------------------------------------------------------------------------------------------------------------------------|------------------------------------|----------------|
| **home**    | Elasticsearch のホームディレクトリ                                                                                        | `/usr/share/elasticsearch`         | `$ES_HOME`     |
| **bin**     | ノード起動のための `elasticsearch` やプラグインをインストールするための `elasticsearch-plugin` などのバイナリスクリプト群 | `/usr/share/elasticsearch/bin`     |                |
| **conf**    | `elasticsearch.yml` を含む設定ファイル群                                                                                  | `/etc/elasticsearch`               | `ES_PATH_CONF` |
| **conf**    | ヒープサイズやファイルディスクリプタを始めとした環境変数                                                                  | `/etc/sysconfig/elasticsearch`     |                |
| **data**    | 各 index や node に割り当てられた shard のデータファイルの配置先                                                          | `/var/lib/elasticsearch`           | `path.data`    |
| **jdk**     | Elaticsearch が起動するためにバンドルされた JDK, `/etc/sysconfig/elasticsearch` 内の `ES_JAVA_HOME` で上書き可能          | `/usr/share/elasticsearch/jdk`     |                |
| **logs**    | ログファイル群                                                                                                            | `/var/log/elasticsearch`           | `path.logs`    |
| **plugins** | プラグインのファイル群, 各プラグインはこのサブディレクトリ内に配置される                                                  | `/usr/share/elasticsearch/plugins` |                |
| **repo**    | 共有ファイルシステムのリポジトリの場所 (複数指定可能)<br />バックアップ / リストアなどで利用                              | 設定なし                           | `path.repo`    |

## 次のステップ
Elasticsearch のテスト環境が整いました。
一方で、本格的な開発や本番環境を構築する前に、いくつか追加の設定が必要になります。

* [Elasticsearch の設定を学ぶ][Configure Elasticsearch]
* [重要な Elasticsearch の設定][important Elasticsearch settings]
* [重要なシステムの設定][important system settings]

[Configure Elasticsearch]: {{< relref "../configure/_index.md" >}}
[important Elasticsearch settings]: {{< relref "../configure/important.md" >}}
[important system settings]: https://www.elastic.co/guide/en/elasticsearch/reference/current/system-config.html
