package me.lasta.studyelasticsearchwithktor.converter.country

import kotlinx.serialization.Serializable

// TODO: change names from snake_case to camelCase
@Serializable
data class NaturalEarthCountry(
    val featurecla: String,
    val scalerank: Int,
    val labelrank: Int,
    val sovereignt: String,
    val sov_a3: String,
    val adm0_dif: Int,
    val level: Int,
    val type: String,
    val admin: String,
    val adm0_a3: String,
    val geou_dif: Int,
    val geounit: String,
    val gu_a3: String,
    val su_dif: Int,
    val subunit: String,
    val su_a3: String,
    val brk_diff: Int,
    val name: String,
    val name_long: String,
    val brk_a3: String,
    val brk_name: String,
    val brk_group: String,
    val abbrev: String,
    val postal: String,
    val formal_en: String,
    val formal_fr: String,
    val name_ciawf: String,
    val note_adm0: String,
    val note_brk: String,
    val name_sort: String,
    val name_alt: String,
    val mapcolor7: Int,
    val mapcolor8: Int,
    val mapcolor9: Int,
    val mapcolor13: Int,
    val pop_est: Long,
    val pop_rank: Int,
    val gdp_md_est: Double,
    val pop_year: Int,
    val lastcensus: Int,
    val gdp_year: Int,
    val economy: String,
    val income_grp: String,
    val wikipedia: Int,
    val fips_10_: String,
    val iso_a2: String,
    val iso_a3: String,
    val iso_a3_eh: String,
    val iso_n3: String,
    val un_a3: String,
    val wb_a2: String,
    val wb_a3: String,
    val woe_id: Int,
    val woe_id_eh: Int,
    val woe_note: String,
    val adm0_a3_is: String,
    val adm0_a3_us: String,
    val adm0_a3_un: Int,
    val adm0_a3_wb: Int,
    val continent: String,
    val region_un: String,
    val subregion: String,
    val region_wb: String,
    val name_len: Int,
    val long_len: Int,
    val abbrev_len: Int,
    val tiny: Int,
    val homepart: Int,
    val min_zoom: Double,
    val min_label: Double,
    val max_label: Double,
    val ne_id: Long,
    val wikidataid: String,
    val name_ar: String,
    val name_bn: String,
    val name_de: String,
    val name_en: String,
    val name_es: String,
    val name_fr: String,
    val name_el: String,
    val name_hi: String,
    val name_hu: String,
    val name_id: String,
    val name_it: String,
    val name_ja: String,
    val name_ko: String,
    val name_nl: String,
    val name_pl: String,
    val name_pt: String,
    val name_ru: String,
    val name_sv: String,
    val name_tr: String,
    val name_vi: String,
    val name_zh: String,
)
