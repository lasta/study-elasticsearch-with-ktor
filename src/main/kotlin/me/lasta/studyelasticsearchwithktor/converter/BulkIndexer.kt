package me.lasta.studyelasticsearchwithktor.converter

import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.netty.handler.codec.http.HttpScheme
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.lasta.studyelasticsearchwithktor.converter.country.IndexAction

class BulkIndexer(
    val host: String = "localhost",
    val port: Int = 9200,
    val scheme: HttpScheme = HttpScheme.HTTP,
) {
    suspend inline fun <reified T> index(
        bulkData: Sequence<Pair<IndexAction, T>>,
        indexName: String,
        deleteBeforeIndexing: Boolean = false,
        httpClient: HttpClient = HttpClient(Apache)
    ): HttpResponse =
        httpClient.use { client ->
            if (deleteBeforeIndexing) {
                val deletionResponse: HttpResponse = client.post(
                    host = host,
                    port = port,
                    scheme = scheme.toString(),
                    path = "$indexName/_delete_by_query"
                ) {
                    contentType(ContentType.Application.Json)
                    body = MATCH_ALL_QUERY
                }

                if (deletionResponse.status != HttpStatusCode.OK) {
                    throw IllegalStateException("Failed to delete all documents.")
                }
            }
            client.put(
                host = host,
                port = port,
                scheme = scheme.toString(),
                path = "/_bulk"
            ) {
                header("Content-Type", "application/x-ndjson")
                body = bulkData.joinToString("\n") { (action, document) ->
                    Json.encodeToString(action) + "\n" + Json.encodeToString(document)
                } + "\n"
            }
        }

    companion object {
        const val MATCH_ALL_QUERY: String = """
            {
                "query": {
                    "match_all": {}
                }
            }
        """
    }
}
