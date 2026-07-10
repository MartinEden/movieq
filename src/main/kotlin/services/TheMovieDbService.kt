package eden.movieq.services

import eden.movieq.models.Movie
import eden.movieq.models.MovieShortDetails
import eden.movieq.models.imdb.ImageInfo
import eden.movieq.models.themoviedb.MovieDetails
import eden.movieq.models.themoviedb.SearchResultCollection
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import kotlin.math.roundToInt

const val missingTokenMessage = """
config.json must provide a bearer token for TheMovieDB. 
e.g. { "the_movie_db_token": "abcdedfg..." }
"""

class TheMovieDbService(token: String?) : MovieService {
    private val endpointUrl = "https://api.themoviedb.org/3/"
    private val imageEndpointUrl = "https://image.tmdb.org/t/p/"
    private val imageSize = "w154"

    private val token = token ?: throw Exception(missingTokenMessage)

    @OptIn(ExperimentalSerializationApi::class)
    private val client: HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                namingStrategy = JsonNamingStrategy.SnakeCase
                ignoreUnknownKeys = true
            })
        }
    }

    private val thumbnailClient = ThumbnailClient(client)

    override fun search(query: String, moreResults: Int): List<MovieShortDetails> {
        val result: SearchResultCollection = callAPI {
            runBlocking {
                client.get("$endpointUrl/search/movie") {
                    bearerAuth(token)
                    url {
                        parameter("query", query)
                        parameter("page", moreResults + 1)
                    }
                }
            }
        }
        return result.results.map {
            MovieShortDetails(
                id = it.id.toString(),
                title = it.title,
                imageUrl = absoluteImageUrl(it.posterPath),
                startYear = yearFromDateString(it.releaseDate)
            )
        }
    }

    override fun get(movieId: String, reason: String): Movie {
        val m: MovieDetails = callAPI {
            runBlocking {
                client.get("$endpointUrl/movie/$movieId") {
                    bearerAuth(token)
                }
            }
        }
        return Movie(
            imdbId = m.imdbId,
            reason = reason,
            title = m.title,
            synopsis = m.overview,
            year = yearFromDateString(m.releaseDate),
            dateAdded = java.time.LocalDate.now(),
            rating = (m.voteAverage * 10).roundToInt(),
            thumbnail = thumbnailClient.downloadThumbnailAndGetPath(
                m.imdbId,
                m.title,
                absoluteImageUrl(m.posterPath)
            ),
            tags = m.genres.map { it.name.lowercase() }
        )
    }

    private fun yearFromDateString(date: String?): Int? = if (date.isNullOrBlank()) {
        null
    } else {
        LocalDate.parse(date).year
    }

    private fun absoluteImageUrl(posterPath: String?): String = if (posterPath != null) {
        "$imageEndpointUrl/$imageSize/$posterPath"
    } else {
        ImageInfo.default.url
    }

    private inline fun <reified T> callAPI(crossinline request: () -> HttpResponse): T {
        return runBlocking {
            val response = request()
            try {
                response.body()
            } catch (e: Exception) {
                throw Exception("Problem parsing API response: ${response.bodyAsText()}", e)
            }
        }
    }
}