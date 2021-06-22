---
title: "テキスト解析"
date: 2021-06-18T15:20:27+09:00
draft: false
---

テキスト解析とは、メールの本文や商品説明などの非構造化テキストを、検索に最適化された構造化フォーマットに変換する処理です。

## テキスト解析をいつ行うのか
テキストフィールドの index 作成時や検索時にテキスト解析を行います。

index にテキストフィールドが含まれていない場合、設定は不要です。
(この章は読み飛ばして問題ありません)

テキストフィールドを使用している場合や、テキスト検索で期待通りの結果が得られない場合は、テキスト解析をすることで解決することが多いです。
また、解析の設定を理解する必要があります。

* 検索エンジンの構築
* 非構造化データの収集
* 特定の言語に特化した検索
* 辞書学や言語学の研究

## 本章の内容
* 概要
* コンセプト
* テキスト解析の構成
* ビルトインの解析器の説明
* 形態素解析器 (tokenizer)
* token filter
* character filter
* normalizer