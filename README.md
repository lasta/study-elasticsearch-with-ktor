# Study Elasticsearch with Ktor

* [Elasticsearch][elasticsearch]
* [Ktor][ktor]

[elasticsearch]: https://www.elastic.co/jp/elasticsearch/
[ktor]: https://ktor.io/

## Building document
see: https://gohugo.io/getting-started/quick-start/

### Setup
```shell
brew install hugo
cd docs
hugo new site "study-elasticsearch-with-ktor"
git submodule add git@github.com:thegeeklab/hugo-geekdoc.git docs/src/themes/hugo-geekdoc
```

### Build
```shell
cd docs/src
hugo -D
```
