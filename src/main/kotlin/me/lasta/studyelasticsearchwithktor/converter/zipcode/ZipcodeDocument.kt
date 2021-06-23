package me.lasta.studyelasticsearchwithktor.converter.zipcode

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ZipcodeDocument(
    @SerialName("admin_code")
    val adminCode: String, // 全国地方公共団体コード(JIS X0401、X0402)
    @SerialName("zipcode5")
    val zipcode5: String, // (旧)郵便番号(5桁)
    @SerialName("zipcode")
    val zipcode: String, // 郵便番号(7桁)
    @SerialName("prefecture_ruby")
    val prefectureRuby: String, // 都道府県名 (半角カタカナ)
    @SerialName("city_ruby")
    val cityRuby: String, // 市区町村名 (半角カタカナ)
    @SerialName("town_ruby")
    val townRuby: String, // 町域名 (半角カタカナ)
    @SerialName("prefecture_name")
    val prefectureName: String, // 都道府県名 (漢字)
    @SerialName("city_name")
    val cityName: String, // 市区町村名 (漢字)
    @SerialName("town_name")
    val townName: String, // 町域名 (漢字)
    @SerialName("represents_by_plural_codes")
    val representsByPluralCodes: Boolean, // 一町域が二以上の郵便番号で表される場合の表示 (1: 該当)
    @SerialName("assigned_street_number_to_each_subdivision")
    val assignedStreetNumberToEachSubdivision: Boolean, // 小字毎に番地が起番されている町域の場合の表示 (1: 該当)
    @SerialName("has_city_block")
    val hasCityBlock: Boolean, // 丁目を有する町域の場合の表示 (1: 該当)
    @SerialName("represents_plural_towns")
    val representsPluralTowns: Boolean, // 一つの郵便番号で二以上の町域を表す場合の表示 (1: 該当)
    @SerialName("update_status")
    val updateStatus: Int, // 更新の表示
                            // 0 : 変更なし
                            // 1 : 変更あり
                            // 2 : 廃止 (廃止データのみ使用)
    @SerialName("update_reason")
    val updateReason: Int, // 変更理由
                            // 0 : 変更なし
                            // 1 : 市政・区政・町政・分区・政令指定都市施行
                            // 2 : 住居表示の実施
                            // 3 : 区画整理
                            // 4 : 郵便区調整等
                            // 5 : 訂正
                            // 6 : 廃止 (廃止データのみ使用)
)
