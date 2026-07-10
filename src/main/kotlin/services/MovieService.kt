package eden.movieq.services

import eden.movieq.models.Movie
import eden.movieq.models.MovieShortDetails

interface MovieService {
    fun search(query: String, maxResults: Int = 3): List<MovieShortDetails>
    fun get(movieId: String, reason: String): Movie
}