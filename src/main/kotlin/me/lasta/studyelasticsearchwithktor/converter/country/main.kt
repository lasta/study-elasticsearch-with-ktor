package me.lasta.studyelasticsearchwithktor.converter.country

import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.geotools.data.DataStoreFinder
import org.geotools.data.FeatureSource
import org.geotools.feature.FeatureCollection
import org.opengis.feature.simple.SimpleFeature
import org.opengis.feature.simple.SimpleFeatureType
import org.opengis.filter.Filter
import java.nio.file.Paths
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("path to shp file is required.")
        exitProcess(1)
    }
    val shapeFileUrl = Paths.get(args[0]).toUri().toURL()
    val dataStore = DataStoreFinder.getDataStore(mapOf("url" to shapeFileUrl))
    val typeName = dataStore.typeNames[0]

    val source: FeatureSource<SimpleFeatureType, SimpleFeature> = dataStore.getFeatureSource(typeName)
    val filter: Filter = Filter.INCLUDE

    val collection: FeatureCollection<SimpleFeatureType, SimpleFeature> = source.getFeatures(filter)

    val bulkActions: Sequence<String> = sequence {
        collection.features().use { features ->
            while (features.hasNext()) {
                val feature: SimpleFeature = features.next()
                val document = feature.serialize()
                val action = IndexAction(
                    IndexActionAndMetadata(
                        index = "country",
                        id = feature.id
                    )
                )
                yield(Json.encodeToString(action))
                yield(Json.encodeToString(document))
            }
        }
    }

    val response: HttpResponse = HttpClient(Apache).use { client ->
        runBlocking {
            client.put("http://localhost:9200/_bulk") {
                header("Content-Type", "application/x-ndjson")
                body = bulkActions.joinToString("\n") + "\n"
            }
        }
    }
    println(response)
}
