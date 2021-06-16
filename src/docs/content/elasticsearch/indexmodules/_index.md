---
title: "Index modules"
date: 2021-06-14T16:44:02+09:00
draft: false
---

Index modules は index ごとに作成されるモジュールです。
index のあらゆる面を制御します。

## Index の設定
Index level settings は index ごとに設定されます。

* static
    * Index の作成時または [closed index][closed index] に指定可能な静的な設定です。
* dynamic
    * [update-index-settings][update-index-settings] を用いて動的に変更可能な設定です。

[closed index]: https://www.elastic.co/guide/en/elasticsearch/reference/current/index-modules.html
[update-index-settings]: https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-update-settings.html

{{< hint warning >}}
{{< icon "comment-dots" >}} **注意**

closed index の設定を変更すると、 index の削除および再作成が必要になる場合があります。
{{< /hint >}}

## 静的な設定の項目 - Static index settings
### `index.number_of_shards`
index が持つべき primary shard の数を設定します。
デフォルト値は1で、 index の作成時のみ設定可能です。
closed index では変更不可能です。

{{< hint info >}}
{{< icon "lightbulb" >}} **Note**

1 index あたりの shard の数はデフォルトで 1024 個までに制限されています。
リソースの割当によってクラスタを不安定にする可能性のある index が誤って作成されるのを防ぐための安全上の制限です。

すべてのノードで `export ES_JAVA_OPTS="-Des.index.max_number_of_shards=128"` などのように指定することで変更可能です。
{{< /hint >}}

### `index.number_of_routing_shards`
Index の [分割][Split index API] の際に用いられる、 routing shard の数を設定します。

5 shards で `number_of_routing_shards` が 30 の場合、2倍または3倍に分割される可能性があります。

* 5 → 10 → 30 (2つに分けて、さらに3つに分ける)
* 5 → 15 → 30 (3つに分けて、さらに2つに分ける)
* 5 → 60 (6つに分ける)

この設定のデフォルト値は、 index の primary shards の数に依存します。
デフォルトでは最大 1024 個の shard まで 2 の係数で分割できるように設計されています。

[Split index API]: https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-split-index.html

{{< hint info >}}
{{< icon "lightbulb" >}} **Note**

Elasticsearch 7.0.0 以降では、この設定は shard 間でのドキュメントの分割方法に影響します。
カスタムルーティングを仕様した古い index を再 index する際、同じドキュメント配分を維持するためには、 `index.number_of_routing_shards` を
明示的に設定する必要があります。

詳細は [related breaking change](https://www.elastic.co/guide/en/elasticsearch/reference/7.0/breaking-changes-7.0.html#_document_distribution_changes) を確認してください。
{{< /hint >}}

### `index.shard.check_on_startup`
起動時に shard の破損をチェックするかどうか指定します。
破損が検出された場合、 shard を参照することができなくなります。

* `false`
    * デフォルト値
    * shard を参照する際に破損しているかどうかチェックしない
* `checksum`
    * 物理的に破損しているかどうかチェックする
* `true`
    * 破損しているかどうか物理的にも論理的にもチェックする
    * 非常に CPU とメモリを食う
  
{{< hint warning >}}
{{< icon "comment-dots" >}} **注意**

熟練者向けの設定です。

index のサイズが大きい場合、 shard の破損の確認は時間がかかります。
{{< /hint >}}

### `index.codec`
デフォルトでは、保存されたデータは LZ4 で圧縮されます。
`best_compression` を指定することで、 DEFLATE を利用してより圧縮率になりますが、フィールド参照時のパフォーマンスが劣化します。

圧縮の種類を変更した場合、セグメントが結合されたあとに新しい圧縮方式が適用されます。
セグメントの結合は [force merge][force merge] で強制的に行う事ができます。

[force merge]: https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-forcemerge.html

### `index.routing_partition_size`
カスタム [ルーティング][routing] 時に値を送信できる shard の数を指定します。
デフォルト値は1です。 index 作成時のみ設定できます。

この値は `index.number_of_shards` の値が 1 でない限り、 `index.number_of_shards` の値より小さくなければなりません。
詳細は [Routing to an index partition][Routing to an index partition] を参照してください。

[routing]: https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-routing-field.html
[Routing to an index partition]: https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-routing-field.html#routing-index-partition

### `index.soft_deletes.enabled`
{{< hint warning >}}
{{< icon "comment-dots" >}} **注意**

Elasticsearch 7.6.0 で Deprecated になりました。
soft-deletes を無効にした index の作成が非推奨になりました。

将来的にはこの設定項目が削除される予定です。
{{< /hint >}}

index で soft-deletes を有効にするかどうか指定します。
soft-deletes は index 作成時のみ指定可能です。
Elasticsearch 6.5.0 以降で作成された index のみ設定できます。
デフォルト値は `true` です。

### `index.soft_deletes.retention_lease.period`
shard history retention lease が期限切れとみなされるまでの最大保持期間を指定します。
shard history retention lease は、 lucene index のマージ時に soft-deletes されないよう保持します。
soft-deletes がレプリケーションされる前にマージされてしまうと、履歴が不完全な状態になるため、後続の処理が失敗します。

デフォルト値は `12h` (12 時間) です。

### `index.load_fixed_bitset_filters_eagerly`
ネストされたクエリに対し、 [cached filters][cached filters] を遅延読み込みするかどうか指定します。
デフォルト値は `true` で遅延読み込みしません。 (eager load)
`false` で遅延読み込み (lazy load) します。

[cached filters]: https://www.elastic.co/guide/en/elasticsearch/reference/current/query-filter-context.html

### `index.hidden`
index をデフォルトで非表示にするかどうか指定します。
ワイルドカードで index を指定した場合、デフォルトでは非表示の index は返却されません。
リクエストごとに `expand_wildcards` パラメータにて正義よされます。

指定できる値は `true` か `false` で、デフォルトでは `false` です。
