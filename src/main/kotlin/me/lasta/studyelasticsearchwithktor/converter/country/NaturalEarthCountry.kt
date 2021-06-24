package me.lasta.studyelasticsearchwithktor.converter.country

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NaturalEarthCountry(
    @SerialName("featurecla")
    val featurecla: String,
    @SerialName("scalerank")
    val scalerank: Int,
    @SerialName("labelrank")
    val labelrank: Int,
    @SerialName("sovereignt")
    val sovereignt: String,
    @SerialName("sov_a3")
    val sovA3: String,
    @SerialName("adm0_dif")
    val adm0Dif: Int,
    @SerialName("level")
    val level: Int,
    @SerialName("type")
    val type: String,
    @SerialName("admin")
    val admin: String,
    @SerialName("adm0_a3")
    val adm0A3: String,
    @SerialName("geou_dif")
    val geouDif: Int,
    @SerialName("geounit")
    val geounit: String,
    @SerialName("gu_a3")
    val guA3: String,
    @SerialName("su_dif")
    val suDif: Int,
    @SerialName("subunit")
    val subunit: String,
    @SerialName("su_a3")
    val suA3: String,
    @SerialName("brk_diff")
    val brkDiff: Int,
    @SerialName("name")
    val name: String,
    @SerialName("name_long")
    val nameLong: String,
    @SerialName("brk_a3")
    val brkA3: String,
    @SerialName("brk_name")
    val brkName: String,
    @SerialName("brk_group")
    val brkGroup: String,
    @SerialName("abbrev")
    val abbrev: String,
    @SerialName("postal")
    val postal: String,
    @SerialName("formal_en")
    val formalEn: String,
    @SerialName("formal_fr")
    val formalFr: String,
    @SerialName("name_ciawf")
    val nameCiawf: String,
    @SerialName("note_adm0")
    val noteAdm0: String,
    @SerialName("note_brk")
    val noteBrk: String,
    @SerialName("name_sort")
    val nameSort: String,
    @SerialName("name_alt")
    val nameAlt: String,
    @SerialName("mapcolor7")
    val mapcolor7: Int,
    @SerialName("mapcolor8")
    val mapcolor8: Int,
    @SerialName("mapcolor9")
    val mapcolor9: Int,
    @SerialName("mapcolor13")
    val mapcolor13: Int,
    @SerialName("pop_est")
    val popEst: Long,
    @SerialName("pop_rank")
    val popRank: Int,
    @SerialName("gdp_md_est")
    val gdpMdEst: Double,
    @SerialName("pop_year")
    val popYear: Int,
    @SerialName("lastcensus")
    val lastcensus: Int,
    @SerialName("gdp_year")
    val gdpYear: Int,
    @SerialName("economy")
    val economy: String,
    @SerialName("income_grp")
    val incomeGrp: String,
    @SerialName("wikipedia")
    val wikipedia: Int,
    @SerialName("fips_10_")
    val fips10: String,
    @SerialName("iso_a2")
    val isoA2: String,
    @SerialName("iso_a3")
    val isoA3: String,
    @SerialName("iso_a3_eh")
    val isoA3Eh: String,
    @SerialName("iso_n3")
    val isoN3: String,
    @SerialName("un_a3")
    val unA3: String,
    @SerialName("wb_a2")
    val wbA2: String,
    @SerialName("wb_a3")
    val wbA3: String,
    @SerialName("woe_id")
    val woeId: Int,
    @SerialName("woe_id_eh")
    val woeIdEh: Int,
    @SerialName("woe_note")
    val woeNote: String,
    @SerialName("adm0_a3_is")
    val adm0A3Is: String,
    @SerialName("adm0_a3_us")
    val adm0A3Us: String,
    @SerialName("adm0_a3_un")
    val adm0A3Un: Int,
    @SerialName("adm0_a3_wb")
    val adm0A3Wb: Int,
    @SerialName("continent")
    val continent: String,
    @SerialName("region_un")
    val regionUn: String,
    @SerialName("subregion")
    val subregion: String,
    @SerialName("region_wb")
    val regionWb: String,
    @SerialName("name_len")
    val nameLen: Int,
    @SerialName("long_len")
    val longLen: Int,
    @SerialName("abbrev_len")
    val abbrevLen: Int,
    @SerialName("tiny")
    val tiny: Int,
    @SerialName("homepart")
    val homepart: Int,
    @SerialName("min_zoom")
    val minZoom: Double,
    @SerialName("min_label")
    val minLabel: Double,
    @SerialName("max_label")
    val maxLabel: Double,
    @SerialName("ne_id")
    val neId: Long,
    @SerialName("wikidataid")
    val wikidataid: String,
    @SerialName("name_ar")
    val nameAr: String,
    @SerialName("name_bn")
    val nameBn: String,
    @SerialName("name_de")
    val nameDe: String,
    @SerialName("name_en")
    val nameEn: String,
    @SerialName("name_es")
    val nameEs: String,
    @SerialName("name_fr")
    val nameFr: String,
    @SerialName("name_el")
    val nameEl: String,
    @SerialName("name_hi")
    val nameHi: String,
    @SerialName("name_hu")
    val nameHu: String,
    @SerialName("name_id")
    val nameId: String,
    @SerialName("name_it")
    val nameIt: String,
    @SerialName("name_ja")
    val nameJa: String,
    @SerialName("name_ko")
    val nameKo: String,
    @SerialName("name_nl")
    val nameNl: String,
    @SerialName("name_pl")
    val namePl: String,
    @SerialName("name_pt")
    val namePt: String,
    @SerialName("name_ru")
    val nameRu: String,
    @SerialName("name_sv")
    val nameSv: String,
    @SerialName("name_tr")
    val nameTr: String,
    @SerialName("name_vi")
    val nameVi: String,
    @SerialName("name_zh")
    val nameZh: String,
)
