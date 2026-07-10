package eden.movieq.services

import eden.movieq.models.Movie
import eden.movieq.models.MovieShortDetails

interface MovieService {
    fun search(query: String, moreResults: Int = 0): ServiceSearchResult
    fun get(movieId: String, reason: String): Movie
}

data class ServiceSearchResult(
    val movies: List<MovieShortDetails>,
    val moreResultsAvailable: Boolean
)