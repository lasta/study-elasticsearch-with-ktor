---
title: "セットアップ"
date: 2021-06-07T18:42:43+09:00
draft: false
---

参照元ドキュメント : [Set up Elasticsearch](https://www.elastic.co/guide/en/elasticsearch/reference/current/setup.html)

この章には下記に関する情報があります。

* ダウンロード
* インストール
* 起動
* 設定

## サポートされているプラットフォーム
OS や JVM に関する推奨環境は [こちら](https://www.elastic.co/support/matrix) に記載されています。
サポート外の環境でも動作する可能性はあります。

## Java (JVM) バージョン
Elasticsearch は Java で開発されており、各ディストリビューションには JDK メンテナから提供されたバンドル版の OpenJDK (GPLv2+CE) が含まれています。
Elasticsearch のホームディレクトリ配下の `jdk` ディレクトリに推奨 JVM である OpenJDK が配置されています。

自前でインストールした JVM を利用する場合は、 Java Home のパスを環境変数 `ES_JAVA_HOME` に指定してください。
その場合、バンドルされている Java の LTS バージョンを使用することを推奨します。
既知の不具合を含む Java バージョンが指定されている場合、 Elasticsearch は起動しません。
自前でインストールした JVM を使用する場合、バンドルされている JVM のディレクトリは削除して ok です。
