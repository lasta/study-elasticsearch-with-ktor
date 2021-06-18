---
title: "Analysis"
date: 2021-06-18T15:12:27+09:00
draft: false
---

Index analysis module は文字列フィールドを term に分割するための解析器の構成を登録するためのレジストリとして機能します。

* document を検索可能にするための転置インデックスを作成
* 検索 term を生成する [`match` query][match query] のような高レベルのクエリで使用される

詳細は [テキスト解析][text analysis] の章を参照してください。

[match query]: https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-match-query.html
[text analysis]: {{< relref "/elasticsearch/textanalysis/_index.md" >}}
