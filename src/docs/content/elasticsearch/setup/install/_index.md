---
title: "Installing Elasticsearch"
date: 2021-06-08T11:51:32+09:00
draft: true
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

| Platform       | format   | link                                                          |
|----------------|----------|---------------------------------------------------------------|
| Linux, MacOS   | `tar.gz` | [Install Elasticsearch from archive on Linux or MacOS][targz] |
| Windows        | `zip`    | [Install Elasticsearch with .zip on Windows][winzip]          |
| Debian         | `deb`    | [Install Elasticsearch with Debian Package][deb]              |
| RHEL           | `rpm`    | [Install Elasticsearch with RPM][rpm]                         |
| Windows (beta) | `msi`    | [Install Elasticsearch with Windows MSI Installer][msi]       |
| Docker         |          | [Install Elasticsearch with Docker][docker]                   |
| Homebrew       |          | [Install Elasticsearch on macOS with Homebrew][brew]          |
| Puppet         |          | [puppet-elasticsearch][puppet]                                |
| Chef           |          | [cookbook-elasticsearch][chef]                                |
| Ansible        |          | [ansible-elasticsearch][ansible]                              |

[targz]: https://www.elastic.co/guide/en/elasticsearch/reference/current/targz.html
[winzip]: https://www.elastic.co/guide/en/elasticsearch/reference/current/zip-windows.html
[deb]: https://www.elastic.co/guide/en/elasticsearch/reference/current/deb.html
[rpm]: https://www.elastic.co/guide/en/elasticsearch/reference/current/rpm.html
[msi]: https://www.elastic.co/guide/en/elasticsearch/reference/current/windows.html
[docker]: https://www.elastic.co/guide/en/elasticsearch/reference/7.13/docker.html
[brew]: https://www.elastic.co/guide/en/elasticsearch/reference/7.13/brew.html
[puppet]: https://github.com/elastic/puppet-elasticsearch
[chef]: https://github.com/elastic/cookbook-elasticsearch
[ansible]: https://github.com/elastic/ansible-elasticsearch
