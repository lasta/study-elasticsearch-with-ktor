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

## 静的な設定項目 - Static index settings
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

## 動的な設定項目 - Dynamic index settings
### `index.number_of_replicas`
各 primary shard のレプリカ数を指定します。
デフォルト値は1です。

### `index.auto_expand_replicas`
クラスタのデータノード数に応じて、レプリカ数を自動的に拡張するかどうかを指定します。

* `false` (既定値)
    * 無効
* ダッシュ区切りで指定 (例: `0-5`)
    * 下限と上限を設定
* `0-all`
    * すべてを使用
    
自動拡張されたレプリカ数は [allocation filtering][allocation filtering] ルールのみ考慮され、
[ノードごとの合計シャード数][total shards per node] を始めとした他の割当規則が無視されることに注意してください。
適用可能なルールによってすべてのレプリカが割り当てられない場合、クラスタの滋養帯が `YELLOW` になる可能性があります。

上限が `all` の場合、 [shard allocation awareness][shard allocation awareness] および
[`cluster.routing.allocation.same_shard.host`][cluster.routing.allocation.same_shard.host] は無視されます。

[allocation filtering]: https://www.elastic.co/guide/en/elasticsearch/reference/current/shard-allocation-filtering.html
[total shards per node]: https://www.elastic.co/guide/en/elasticsearch/reference/current/allocation-total-shards.html
[shard allocation awareness]: https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-cluster.html#shard-allocation-awareness
[cluster.routing.allocation.same_shard.host]: https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-cluster.html#cluster-routing-allocation-same-shard-host

### `index.search.idle.after`
shard idle 状態 (検索や get リクエストが来ていない) とみなすまでの時間を指定します。
デフォルト値は 30秒 (`30s`) です。

### `index.refresh_interval`
index に対する直近の変更を検索に反映させるまでのリフレッシュを行う頻度を指定します。
デフォルト値は 1秒 (`1s`) です。
`-1` を指定すると、リフレッシュが無効になります。

この設定が明示的に指定されていない場合、少なくとも `index.search.idle.after` で指定した時間検索トラフィックが来ていない shard は、
検索リクエストが来るまでバックグラウンドでリフレッシュしません。
リフレッシュされていない idle 状態の shard にヒットした場合、次のバックグラウンドでのリフレッシュを待ちます。 (1秒以内)
この動作は、検索が行われない bulk indexing の動作を自動的に最適化することを目的としています。
これを無効にするには、明示的に `1s` を指定する必要があります。

### `index.max_result_window`
検索時の `from + size` の最大値を指定します。
デフォルト値は 10000 です。

`from + size` に大きな値を指定するとメモリと時間を消費する (ディープページング問題) ための対策です。

この値を上げない効率的な方法については、 [Scroll][Scroll] と [Search After][Search After] を参照してください。

[Scroll]: https://www.elastic.co/guide/en/elasticsearch/reference/current/paginate-search-results.html#scroll-search-results
[Search After]: https://www.elastic.co/guide/en/elasticsearch/reference/current/paginate-search-results.html#search-after 

### `index.max_inner_result_window` 
inner hits や top hits aggregations の際の `from + size` の上限値を指定します。
デフォルト値は 100 です。

inner hits や top hits aggregations は `from + size` に比例してメモリと時間を消費します。

### `index.max_rescore_window`
`rescore` の `window_size` の最大値を指定します。
デフォルト値は `index.max_result_window` です。

`max(window_size, from + size)` に比例してしてメモリと時間を消費します。

### `index.max_docvalue_fields_search`
クエリで許容される `docvalue_fields` の最大値を指定します。
デフォルト値は 100 です。

Doc-value フィールドは、各ドキュメントの各フィールドごとに seek が発生する場合があります。

### `index.max_script_fields`
1クエリでの `script_fields` の個数の上限値を指定します。
デフォルト値は 32 です。

### `index.max_ngram_diff`
`NGramTokenizer` や `NGramTokenFilter` における `min_gram` と `max_gram` 間の最大の差を指定します。
デフォルト値は1です。

### `index.max_shingle_diff`
[`shingle` token filter][shingle token filter] における `max_shingle_size` と `min_shingle_size` 間の最大の差を指定します。
デフォルト値は 3 です。

[shingle token filter]: https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-shingle-tokenfilter.html

### `index_max_refresh_listeners`
index の各 shard で利用可能な refresh listener の最大数を指定します。
この listener は [`refresh=wait_for`][refresh parameter] で使用されます。

[refresh parameter]: https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-refresh.html

### `index.analyze.max_token_count`
生成される token 数の最大値を指定します。 `analyze` API で使用されます。
デフォルト値は 10000 です。

### `index.highlight.max_analyzed_offset`
highlight リクエストにて解析される文字の最大数を指定します。
この設定は、 offset または term vector なしで index されたテキストに対し highlight が要求された場合のみ適用されます。
デフォルト値は `1000000` です。

### `index.max_terms_count`
Terms Query における terms 数の最大値を指定します。
デフォルト値は 65536 です。

### `index.max_regex_length`
Regexp Query における正規表現の長さの上限を指定子ます。
デフォルト値は 1000 です。

### `index.query.default_field`
デフォルトの検索対象のフィールドを指定します。
文字列または文字列の配列を指定できます。

このフィールドは下記クエリで利用されます。

* [More like this]: https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-mlt-query.html
* [Multi-match]: https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-multi-match-query.html
* [Query string]: https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-query-string-query.html
* [Simple query string]: https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-simple-query-string-query.html

デフォルト値は `*` です。
メタデータフィールドを除く、 term-level クエリの対象となるすべてのフィールドを対象とします。

### `index.routing.allocation.enable`
shard の割当を制御します。

* `all` (デフォルト値)
    * すべての shard に対して shard allocation を許可する
* `primaries`
    * primary shard のみ shard allocation を許可する
* `new_primaries`
    * 新たに作成された primary shard のみ shard allocation を許可する
* `none`
    * shard allocation をしない
    

### `index.routing.rebalance.enable`
shard の再バランシング (shard rebalancing) を有効化します。

* `all` (デフォルト値)
    * すべての shard
* `primaries`
    * primary shard のみ
* `replicas`
    * replica shard のみ
* `none`
    * 許可しない
    
### `index.gc_deletes`
[削除された document のバージョン番号][deleted document’s version number] が
[その後のバージョン操作][further versioned operations] によって再利用可能になるまでの時間を指定します。

デフォルト値は 60秒 (`60s`) です。

[deleted document’s version number]: https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-delete.html#delete-versioning
[further versioned operations]: https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-index_.html#index-versioning

### `index.default_pipeline`
デフォルトの [ingest node][ingest node] パイプラインを指定します。
デフォルトのパイプラインが設定されていて、かつそのパイプラインが存在しない場合、 index へのリクエストは失敗します。

`pipeline` パラメータを使用して上書きできます。

特殊なパイプライン名 `_none` は、 ingest pipeline を実行しないことを示します。

[ingest node]: https://www.elastic.co/guide/en/elasticsearch/reference/current/ingest.html

### `inex.final_pipeline`
index の final [ingest node] パイプラインを指定します。
final pipeline が指定され、かつその pipeline が存在しない場合、 index へのリクエストは失敗します。

final pipeline は、 request pipeline (指定されている場合) と default pipelin (存在する場合) のあとに常に実行されます。

特殊な pipeline 名 `_none` は、 ingest pipeline が実行されないことを示します。

## 他の index modules の設定
* [Analysis][Analysis]
    * 解析器 (analizers)、形態素解析器 (tokenizers)、トークンフィルタ (token filters)、キャラクタフィルタ (character filters) の設定
* [Index shard allocation][Index shard allocation]
    * node に対し、いつどこにどうやって shard が配置されるかどうかの設定
* [Mapping][Mapping]
    * 動的 mapping の有効 / 無効の設定
* [Merging][Merging]
    * バックグラウンド実行される merge process によって shard がどうマージされるかどうかの設定
* [Similarities][Similarities]
    * 検索結果のスコアリングのカスタマイズのための、類似度をカスタマイズするための設定
* [Slowlog][Slowlog]
    * query と fetch の slow-log の設定
* [Store][Store]
    * shard データへアクセスする際のファイルシステムの種類の設定
* [Translog][Translog]
    * トランザクションログとバックグラウンド実行される flush 処理の設定
* [History retention][History retention]
    * index の操作履歴の設定
* [Indexing pressure][Indexing pressure]
    * index データの圧縮に関する設定

[Analysis]: https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis.html
[Index shard allocation]: https://www.elastic.co/guide/en/elasticsearch/reference/current/index-modules-allocation.html
[Mapping]: https://www.elastic.co/guide/en/elasticsearch/reference/current/index-modules-mapper.html
[Merging]: https://www.elastic.co/guide/en/elasticsearch/reference/current/index-modules-merge.html
[Similarities]: https://www.elastic.co/guide/en/elasticsearch/reference/current/index-modules-merge.html
[Slowlog]: https://www.elastic.co/guide/en/elasticsearch/reference/current/index-modules-slowlog.html
[Store]: https://www.elastic.co/guide/en/elasticsearch/reference/current/index-modules-store.html
[Translog]: https://www.elastic.co/guide/en/elasticsearch/reference/current/index-modules-translog.html
[History retention]: https://www.elastic.co/guide/en/elasticsearch/reference/current/index-modules-history-retention.html
[Indexing pressure]: https://www.elastic.co/guide/en/elasticsearch/reference/current/index-modules-indexing-pressure.html
