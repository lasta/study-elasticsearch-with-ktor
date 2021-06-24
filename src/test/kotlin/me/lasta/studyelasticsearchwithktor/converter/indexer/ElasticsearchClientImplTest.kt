package me.lasta.studyelasticsearchwithktor.converter.indexer

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteReadPacket
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.utils.io.readUTF8Line
import io.ktor.utils.io.streams.readerUTF8
import kotlinx.coroutines.runBlocking
import me.lasta.studyelasticsearchwithktor.converter.indexer.entity.IndexAction
import me.lasta.studyelasticsearchwithktor.converter.indexer.entity.IndexActionAndMetadata
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class ElasticsearchClientImplTest {

    private lateinit var httpClient: HttpClient
    private lateinit var elasticsearchClient: ElasticsearchClientImpl

    @Nested
    inner class BulkIndexTest {

        private val metadataTemplate = IndexActionAndMetadata(
            index = "index_name",
            id = "TODO"
        )

        private val bulkData = sequence {
            yield(IndexAction(metadataTemplate.copy(id = "1")) to SampleDocument("value1"))
            yield(IndexAction(metadataTemplate.copy(id = "2")) to SampleDocument("value2"))
        }

        private val serializer = SampleDocument.serializer()

        @BeforeEach
        fun setUp() {
            httpClient = HttpClient(MockEngine) {
                engine {
                    addHandler { request ->
                        assertEquals(HttpMethod.Put, request.method)
                        assertEquals(Url("http://localhost:9200/_bulk"), request.url)
                        val actualBodyContentType = request.body.contentType.toString()
                        assertEquals("application/x-ndjson", actualBodyContentType)
                        val actualBodyContent = request.body.toByteReadPacket().copy().readerUTF8().readText()
                        val expectedBodyContent = """
                            {"index":{"_index":"index_name","_id":"1"}}
                            {"key":"value1"}
                            {"index":{"_index":"index_name","_id":"2"}}
                            {"key":"value2"}
                            """.trimIndent() + "\n"
                        assertEquals(expectedBodyContent, actualBodyContent)

                        respond("OK")
                    }
                }
            }

            elasticsearchClient = ElasticsearchClientImpl(httpClientSupplier = { httpClient })
        }

        @Test
        fun requestsSuccessfully() {
            val actual = runBlocking { elasticsearchClient.bulkIndex(bulkData, serializer) }
            assertEquals(HttpStatusCode.OK, actual.status)
            val actualContent = runBlocking {
                actual.content.readUTF8Line()
            }
            assertEquals("OK", actualContent)
        }
    }

    @Nested
    inner class DeleteAllTest {

        private val indexName = "index_name"

        @BeforeEach
        fun setUp() {
            httpClient = HttpClient(MockEngine) {
                engine {
                    addHandler { request ->
                        assertEquals(HttpMethod.Post, request.method)
                        assertEquals(Url("http://localhost:9200/${indexName}/_delete_by_query"), request.url)
                        assertEquals(ContentType.Application.Json, request.body.contentType)
                        val actualBodyContent =
                            request.body.toByteReadPacket().copy().readerUTF8().readText().trimIndent()
                        val expectedBodyContent = """
                            {
                                "query": {
                                    "match_all": {}
                                }
                            }
                            """.trimIndent()
                        assertEquals(expectedBodyContent, actualBodyContent)

                        respond("OK")
                    }
                }
            }

            elasticsearchClient = ElasticsearchClientImpl(httpClientSupplier = { httpClient })
        }

        @Test
        fun requestsSuccessfully() {
            val actual = runBlocking { elasticsearchClient.deleteAll(indexName) }
            assertEquals(HttpStatusCode.OK, actual.status)
            val actualContent = runBlocking {
                actual.content.readUTF8Line()
            }
            assertEquals("OK", actualContent)
        }
    }
}
