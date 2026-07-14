package eden.movieq.services

import eden.movieq.models.Movie
import eden.movieq.models.sql.MovieEntity

interface StorageService {
    val all: Iterable<Movie>
    val tags: Iterable<String>
    fun insert(movie: Movie)
    // Returns the updated entity that conforms to this condition, or null if either no entity was
    // found or if more than one entity conforms to the condition.
    fun updateMovie(imdbId: String, changes: (MovieEntity) -> Unit): MovieEntity?


//    fun get(imdbId: String): Movie?
}