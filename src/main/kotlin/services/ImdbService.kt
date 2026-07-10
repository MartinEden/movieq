package eden.movieq.services

import eden.movieq.MovieQApp
import eden.movieq.models.imdb.FullTitleInfo
import eden.movieq.models.imdb.ImageInfo
import eden.movieq.models.imdb.ImageSearchResult
import eden.movieq.models.Movie
import eden.movieq.models.MovieShortDetails
import eden.movieq.models.imdb.SearchResult
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
import kotlinx.serialization.json.Json
import java.io.File
import java.time.LocalDate

class ImdbService(val endpointURL: String): MovieService {
    private val client: HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    override fun search(query: String, maxResults: Int): List<MovieShortDetails> {
        val result: SearchResult = runBlocking {
            val response = client.get("$endpointURL/search/titles") {
                url {
                    parameter("query", query)
                    parameter("limit", maxResults)
                }
            }
            response.body()
        }
        return result.titles.map {
            MovieShortDetails(
                id = it.id,
                title = it.primaryTitle,
                imageUrl = it.primaryImage.url,
                startYear = it.startYear
            )
        }
    }

    private fun downloadThumbnailAndGetPath(movieId: String, title: String, fallback: ImageInfo?): String {
        val info = getPosterUrl(movieId) ?: fallback
        return if (info != null) {
            runBlocking {
                val fileName = generateThumbnailFileName(movieId, title)
                val file = File(MovieQApp.THUMBNAIL_DIRECTORY, fileName)
                client.get(info.url).bodyAsChannel().copyAndClose(file.writeChannel())
                MovieQApp.logger.info("Saved thumbnail to ${file.path}")
                "/static/thumbnails/$fileName"
            }
        } else {
            ImageInfo.default.url
        }
    }

    private fun getPosterUrl(movieId: String): ImageInfo? {
        val images: ImageSearchResult = runBlocking {
            client.get("$endpointURL/titles/$movieId/images") {
                parameter("types", "POSTER")
                // This actually means how many images (URLs) to return
                parameter("pageSize", 1)
            }.body()
        }
        return images.images.firstOrNull()
    }

    override fun get(movieId: String, reason: String): Movie {
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
            tomatoMeter = null, // Filled in later
            thumbnail = downloadThumbnailAndGetPath(movieId, title.primaryTitle, fallback = title.primaryImage),
            tags = title.genres.map { it.lowercase() }
        )
    }

    fun generateThumbnailFileName(movieId: String, title: String)
        = movieId + "-" + title.lowercase().replace(Regex("""\W+"""), "-");
}