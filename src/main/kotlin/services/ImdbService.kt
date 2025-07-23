package eden.movieq.services

import eden.movieq.models.FullTitleInfo
import eden.movieq.models.SearchResult
import eden.movieq.models.TitleInfo
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
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