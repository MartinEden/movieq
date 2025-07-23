package eden.movieq.services

import eden.movieq.MovieQApp
import eden.movieq.models.FullTitleInfo
import eden.movieq.models.ImageInfo
import eden.movieq.models.Movie
import eden.movieq.models.SearchResult
import eden.movieq.models.TitleInfo
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsChannel
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.copyAndClose
import kotlinx.coroutines.runBlocking
import java.io.File
import java.time.LocalDate

class ImdbService(val endpointURL: String) {
    private val client: HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    fun search(query: String, maxResults: Int = 3): List<TitleInfo> {
        val result: SearchResult = runBlocking {
            client.get("$endpointURL/search/titles") {
                url {
                    parameter("query", query)
                    parameter("limit", maxResults)
                }
            }.body()
        }
        return result.titles
    }

    private fun downloadThumbnailAndGetPath(movieId: String, info: ImageInfo?): String {
        // TODO: Use /titles/{titleId}/images with posters parameter to get a poster image
        return if (info != null) {
            runBlocking {
                val file = File(MovieQApp.thumbnailPath, movieId)
                client.get(info.url).bodyAsChannel().copyAndClose(file.writeChannel())
                "/static/thumbnails/$movieId"
            }
        } else {
            ImageInfo.default.url
        }
    }

    fun get(movieId: String, reason: String): Movie {
        val title: FullTitleInfo = runBlocking {
            client.get("$endpointURL/titles/$movieId").body()
                ?: throw Exception("Couldn't find title $movieId in IMDB service")
        }
        return Movie(
            imdbId = movieId,
            reason = reason,
            title = title.primaryTitle,
            synopsis = title.plot ?: "",
            year = title.startYear,
            dateAdded = LocalDate.now(),
            rating = title.rating?.aggregateRating?.times(10)?.toInt(),
            tomatoMeter = null, // TODO: Tomato Meter
            thumbnail = downloadThumbnailAndGetPath(movieId, title.primaryImage),
            tags = title.genres.map { it.lowercase() }
        )
    }
}