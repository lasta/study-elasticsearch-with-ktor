# Study Elasticsearch with Ktor
[![github pages](https://github.com/lasta/study-elasticsearch-with-ktor/actions/workflows/gh-pages.yml/badge.svg)](https://github.com/lasta/study-elasticsearch-with-ktor/actions/workflows/gh-pages.yml)
[![Test application](https://github.com/lasta/study-elasticsearch-with-ktor/actions/workflows/ktor.yml/badge.svg)](https://github.com/lasta/study-elasticsearch-with-ktor/actions/workflows/ktor.yml)

* [Elasticsearch][elasticsearch]
* [Ktor][ktor]

[elasticsearch]: https://www.elastic.co/jp/elasticsearch/
[ktor]: https://ktor.io/

## Document
https://lasta.github.io/study-elasticsearch-with-ktor/

### Build document
see [src/docs/README.md](src/docs/README.md)

## Country data converter
Converter from "Admin 0 - Countries" shapefile by Natural Earth to NDJSON for Elasticsearch to index.

### before to run
1. download countries archive from [this page](https://www.naturalearthdata.com/downloads/10m-cultural-vectors/)
2. unzip it
3. put shapefile into data

```
etc/converter/country/data
└── ne_10m_admin_0_countries.shp
```

