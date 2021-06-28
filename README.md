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
3. put files into `data/country`

```
etc/converter/country/data
└── ne_10m_admin_0_countries.shp
```

## Zipcode data converter
Converter from "13TOKYO.CSV" csv file by JAPAN POST HOLDINGS Co., Ltd.

### before to run
1. download zipcode archive from [this page](https://www.post.japanpost.jp/zipcode/dl/kogaki-zip.html)
    * click "東京都" to download it.
2. unzip it
3. put file into `data/zipcode_13tokyo`
4. convert `Shift_JIS` to `UTF-8`
   ```shell
   nkf -Lu -w 13TOKYO.CSV > utf8_13tokyo.csv
   ```

## TODO
- [x] integration test
- [x] unit test
- [ ] implement demo (vue.js? compose?)
- [ ] autocompletion with Japanese input on zipcode index
- [ ] avoid garbling in country index
- [ ] ~~split project~~
