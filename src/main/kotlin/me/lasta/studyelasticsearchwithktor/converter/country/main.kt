package me.lasta.studyelasticsearchwithktor.converter.country

import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import me.lasta.studyelasticsearchwithktor.converter.indexer.ElasticsearchClient
import me.lasta.studyelasticsearchwithktor.converter.indexer.ElasticsearchClientImpl
import me.lasta.studyelasticsearchwithktor.converter.indexer.entity.IndexAction
import me.lasta.studyelasticsearchwithktor.converter.indexer.entity.IndexActionAndMetadata
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

    val bulkData: Sequence<Pair<IndexAction, NaturalEarthCountry>> = sequence {
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
                yield(action to document)
            }
        }
    }

    val elasticsearchClient: ElasticsearchClient = ElasticsearchClientImpl()
    val deleteResponse = runBlocking {
        elasticsearchClient.deleteAll("country")
    }
    if (deleteResponse.status != HttpStatusCode.OK) {
        throw IllegalStateException(deleteResponse.toString())
    }
    println(deleteResponse)

    val response = runBlocking {
        elasticsearchClient.bulkIndex(bulkData, NaturalEarthCountry.serializer())
    }

    println(response)
}
