package me.lasta.studyelasticsearchwithktor.converter.zipcode

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.csv.Csv
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

    // TODO implement indexing process
    println(records[0])


}
