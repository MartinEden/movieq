package eden.movieq.services

import eden.movieq.models.Movie

interface StorageService {
    val all: Iterable<Movie>
    fun save(movie: Movie)
}