---
title: "Installing"
date: 2021-06-08T11:51:32+09:00
draft: false
---

参照元ドキュメント : [Installing Elasticsearch][Installing Elasticsearch]

[Installing Elasticsearch]: https://www.elastic.co/guide/en/elasticsearch/reference/current/install-elasticsearch.html

自前のサーバにインストールする方法と、 SaaS を利用する方法があります。

## SaaS (Hosted Elasticsearch)
マネージドの Elasticsearch と Kibana は、
[Amazon Web Service](https://aws.amazon.com/marketplace/pp/Elasticsearch-Inc-Elasticsearch-Service-on-Elastic/prodview-voru33wi6xs7k) 、
[Google Cloud Platform](https://console.cloud.google.com/marketplace/product/endpoints/elasticsearch-service.gcpmarketplace.elastic.co?pli=1&project=round-concept-238704&folder=&organizationId=) 、
[Microsoft Azure](https://azuremarketplace.microsoft.com/en-us/marketplace/apps/elastic.ec-azure?tab=Overview)
で利用可能です。

[Elasticsearch Service](https://www.elastic.co/jp/elasticsearch/service)

## 自前サーバにインストール
下記のパッケージフォーマットで提供されいてます。

| Platform       | format   | link                                                                                |
|----------------|----------|-------------------------------------------------------------------------------------|
| Linux, MacOS   | `tar.gz` | [Install Elasticsearch from archive on Linux or MacOS][targz]                       |
| Windows        | `zip`    | [Install Elasticsearch with .zip on Windows][winzip] {{< icon "share-square" >}}    |
| Debian         | `deb`    | [Install Elasticsearch with Debian Package][deb] {{< icon "share-square" >}}        |
| RHEL           | `rpm`    | [Install Elasticsearch with RPM][rpm]                                               |
| Windows (beta) | `msi`    | [Install Elasticsearch with Windows MSI Installer][msi] {{< icon "share-square" >}} |
| Docker         |          | [Install Elasticsearch with Docker][docker]                                         |
| Homebrew       |          | [Install Elasticsearch on macOS with Homebrew][brew] {{< icon "share-square" >}}    |
| Puppet         |          | [puppet-elasticsearch][puppet] {{< icon "share-square" >}}                          |
| Chef           |          | [cookbook-elasticsearch][chef] {{< icon "share-square" >}}                          |
| Ansible        |          | [ansible-elasticsearch][ansible] {{< icon "share-square" >}}                        |

[targz]: {{< relref "targz.md" >}}
[winzip]: https://www.elastic.co/guide/en/elasticsearch/reference/current/zip-windows.html
[deb]: https://www.elastic.co/guide/en/elasticsearch/reference/current/deb.html
[rpm]: {{< relref "rpm.md" >}}
[msi]: https://www.elastic.co/guide/en/elasticsearch/reference/current/windows.html
[docker]: {{< relref "docker.md" >}}
[brew]: https://www.elastic.co/guide/en/elasticsearch/reference/7.13/brew.html
[puppet]: https://github.com/elastic/puppet-elasticsearch
[chef]: https://github.com/elastic/cookbook-elasticsearch
[ansible]: https://github.com/elastic/ansible-elasticsearch
