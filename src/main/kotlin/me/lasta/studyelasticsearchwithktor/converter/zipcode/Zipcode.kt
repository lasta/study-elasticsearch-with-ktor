package me.lasta.studyelasticsearchwithktor.converter.zipcode

import kotlinx.serialization.Serializable

/**
 * Data file format
 * see. https://www.post.japanpost.jp/zipcode/dl/readme.html
 */
@Serializable
data class Zipcode(
    val adminCode: String, // 全国地方公共団体コード(JIS X0401、X0402)
    val zipcode3: String, // (旧)郵便番号(5桁)
    val zipcode: String, // 郵便番号(7桁)
    val prefectureRuby: String, // 都道府県名 (半角カタカナ)
    val cityRuby: String, // 市区町村名 (半角カタカナ)
    val townRuby: String, // 町域名 (半角カタカナ)
    val prefectureName: String, // 都道府県名 (漢字)
    val cityName: String, // 市区町村名 (漢字)
    val townName: String, // 町域名 (漢字)
    val representsByPluralCodes: Int, // 一町域が二以上の郵便番号で表される場合の表示 (1: 該当)
    val assignedStreetNumberToEachSubdivision: Int, // 小字毎に番地が起番されている町域の場合の表示 (1: 該当)
    val hasCityBlock: Int, // 丁目を有する町域の場合の表示 (1: 該当)
    val representsPluralTowns: Int, // 一つの郵便番号で二以上の町域を表す場合の表示 (1: 該当)
    val updateStatus: Int, // 更新の表示
                            // 0 : 変更なし
                            // 1 : 変更あり
                            // 2 : 廃止 (廃止データのみ使用)
    val updateReason: Int, // 変更理由
                            // 0 : 変更なし
                            // 1 : 市政・区政・町政・分区・政令指定都市施行
                            // 2 : 住居表示の実施
                            // 3 : 区画整理
                            // 4 : 郵便区調整等
                            // 5 : 訂正
                            // 6 : 廃止 (廃止データのみ使用)
)
