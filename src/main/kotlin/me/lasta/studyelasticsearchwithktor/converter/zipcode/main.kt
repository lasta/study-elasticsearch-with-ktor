package me.lasta.studyelasticsearchwithktor.converter.zipcode

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.csv.Csv
import me.lasta.studyelasticsearchwithktor.converter.BulkIndexer
import me.lasta.studyelasticsearchwithktor.converter.IndexAction
import me.lasta.studyelasticsearchwithktor.converter.IndexActionAndMetadata
import java.nio.file.Paths
import kotlin.system.exitProcess

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

            val document = ZipcodeDocument(
                adminCode = record.adminCode,
                zipcode5 = record.zipcode5,
                zipcode = record.zipcode,
                prefectureRuby = record.prefectureRuby,
                cityRuby = record.cityRuby,
                townRuby = record.townRuby,
                prefectureName = record.prefectureName,
                cityName = record.cityName,
                townName = record.townName,
                representsByPluralCodes = record.representsByPluralCodes == 1,
                assignedStreetNumberToEachSubdivision = record.assignedStreetNumberToEachSubdivision == 1,
                hasCityBlock = record.hasCityBlock == 1,
                representsPluralTowns = record.representsPluralTowns == 1,
                updateStatus = record.updateStatus,
                updateReason = record.updateReason,
            )
            yield(action to document)
        }
    }

    val response = runBlocking {
        BulkIndexer().index(bulkData, indexName = "zipcode", deleteBeforeIndexing = true)
    }
    println(response)
}
