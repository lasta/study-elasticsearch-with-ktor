# document built with Hugo
see: https://gohugo.io/getting-started/quick-start/

1. install hugo
```shell
brew install hugo
```
2. create new site
```shell
hugo new site "study-elasticsearch-with-ktor"
```
3. create [`config.toml`](./config.toml)
    * `publishdir` must be `${repositoryRoot}/docs` because GitHub Pages requires static pages are in root or docs directory.
4. install theme
```shell
# run on ${repositoryRoot}/src/docs
git submodule add git@github.com:thegeeklab/hugo-geekdoc.git themes/hugo-geekdoc
cd themes/hugo-geekdoc
npm install
npx gulp default
```
5. create new post
```shell
hugo new posts/first-post.md
```
    * write `first-post.md`
6. serve
```shell
hugo server -D # -D: include content marked as draft
```
7. build
```shell
hugo
```
8. deploy
```shell
git status
git add -A
git commit
git push
```
9. setup [GitHub Pages](https://pages.github.com/)

