package me.lasta.studyelasticsearchwithktor.converter.indexer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class IndexAction(
    @SerialName("index")
    val index: IndexActionAndMetadata
)

