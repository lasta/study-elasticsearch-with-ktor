package me.lasta.studyelasticsearchwithktor.converter.indexer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SampleDocument(
    @SerialName("key")
    val key: String
)
