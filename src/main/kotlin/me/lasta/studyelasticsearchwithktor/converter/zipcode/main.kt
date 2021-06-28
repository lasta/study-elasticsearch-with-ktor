package me.lasta.studyelasticsearchwithktor.converter.zipcode

import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.csv.Csv
import me.lasta.studyelasticsearchwithktor.converter.indexer.ElasticsearchClient
import me.lasta.studyelasticsearchwithktor.converter.indexer.ElasticsearchClientImpl
import me.lasta.studyelasticsearchwithktor.converter.indexer.entity.IndexAction
import me.lasta.studyelasticsearchwithktor.converter.indexer.entity.IndexActionAndMetadata
import java.nio.file.Paths
import kotlin.system.exitProcess

@OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
fun main(args: Array<String>) {
    if (args.size != 1) {
        println("path to csv file is required.")
        exitProcess(1)
    }

    val csvFile = Paths.get(args[0]).toFile()
    val csv = Csv { hasHeaderRecord = false }
    val records: List<Zipcode> = csv.decodeFromString(ListSerializer(Zipcode.serializer()), csvFile.readText())

    val bulkData: Sequence<Pair<IndexAction, ZipcodeDocument>> = sequence {
        records.forEachIndexed { index, record ->
            val action = IndexAction(
                IndexActionAndMetadata(
                    index = "zipcode",
                    id = index.toString()
                )
            )

            val document = record.toDocument()
            yield(action to document)
        }
    }

    val elasticsearchClient: ElasticsearchClient = ElasticsearchClientImpl()
    val deleteResponse = runBlocking {
        elasticsearchClient.deleteAll("zipcode")
    }

    if (deleteResponse.status != HttpStatusCode.OK) {
        throw IllegalStateException(deleteResponse.toString())
    }
    println(deleteResponse)

    val response = runBlocking {
        elasticsearchClient.bulkIndex(bulkData, ZipcodeDocument.serializer())
    }
    println(response)
}
