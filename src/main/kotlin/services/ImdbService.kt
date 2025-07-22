package eden.movieq.services

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
    fun search(query: String): List<TitleInfo> {
        val result: SearchResult = runBlocking {
            HttpClient(CIO) {
                install(ContentNegotiation) {
                    json()
                }
            }.get("$endpointURL/search/titles") {
                url {
                    parameter("query", query)
                }
            }.body()
        }
        return result.titles
    }
}