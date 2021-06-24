package me.lasta.studyelasticsearchwithktor.converter.indexer

import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.utils.io.core.use
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.lasta.studyelasticsearchwithktor.converter.indexer.entity.IndexAction

internal class ElasticsearchClientImpl(
    private val httpClientSupplier: () -> HttpClient = { HttpClient(Apache) },
    private val baseUrl: String = "http://localhost:9200"
) : ElasticsearchClient {
    override suspend fun <T> bulkIndex(
        bulkData: Sequence<Pair<IndexAction, T>>,
        serializer: SerializationStrategy<T>
    ): HttpResponse = httpClientSupplier().use { client ->
        client.put("$baseUrl/_bulk") {
            header("Content-Type", "application/x-ndjson")
            body = bulkData.joinToString("\n") { (action, document) ->
                Json.encodeToString(action) + "\n" + Json.encodeToString(serializer, document)
            } + "\n"
        }
    }

    override suspend fun deleteAll(indexName: String): HttpResponse = httpClientSupplier().use { client ->
        client.post("$baseUrl/$indexName/_delete_by_query") {
            contentType(ContentType.Application.Json)
            body = MATCH_ALL_QUERY
        }
    }

    companion object {
        private const val MATCH_ALL_QUERY: String = """
            {
                "query": {
                    "match_all": {}
                }
            }
        """
    }
}
