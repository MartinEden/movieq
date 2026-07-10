package eden.movieq.services

import eden.movieq.models.Movie
import eden.movieq.models.MovieShortDetails
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

const val missingTokenMessage = """
config.json must provide a bearer token for TheMovieDB. 
e.g. { "the_movie_db_token": "abcdedfg..." }
"""

class TheMovieDbService(token: String?) : MovieService {
    val endpointUrl = "https://api.themoviedb.org/3/"
    val token = token ?: throw Exception(missingTokenMessage)

    @OptIn(ExperimentalSerializationApi::class)
    private val client: HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                namingStrategy = JsonNamingStrategy.SnakeCase
                ignoreUnknownKeys = true
            })
        }
    }

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
                imageUrl = it.posterPath,   // TODO: Build the correct URL
                startYear = LocalDate.parse(it.releaseDate).year
            )
        }
    }

    override fun get(movieId: String, reason: String): Movie {
        TODO("Not yet implemented")
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