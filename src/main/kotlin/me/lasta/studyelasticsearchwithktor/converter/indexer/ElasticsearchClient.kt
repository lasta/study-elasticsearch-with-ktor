package me.lasta.studyelasticsearchwithktor.converter.indexer

import io.ktor.client.HttpClient
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.SerializationStrategy
import me.lasta.studyelasticsearchwithktor.converter.indexer.entity.IndexAction

interface ElasticsearchClient {

    val httpClientSupplier: () -> HttpClient
    val baseUrl: String

    suspend fun <T> bulkIndex(
        bulkData: Sequence<Pair<IndexAction, T>>,
        serializer: SerializationStrategy<T>
    ): HttpResponse

    suspend fun deleteAll(indexName: String): HttpResponse
}
