package eden.movieq.services

import eden.movieq.models.FullTitleInfo
import eden.movieq.models.ImageInfo
import eden.movieq.models.SearchResult
import eden.movieq.models.TitleInfo
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking

class ImdbService(val endpointURL: String) {
    private val client: HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    fun search(query: String): List<TitleInfo> {
        val result: SearchResult = runBlocking {
            client.get("$endpointURL/search/titles") {
                url {
                    parameter("query", query)
                }
            }.body()
        }
        return result.titles
    }

    fun get(movieId: String): FullTitleInfo? {
        return runBlocking {
            client.get("$endpointURL/titles/$movieId").body()
        }
    }
}