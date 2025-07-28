package eden.movieq.services

import eden.movieq.models.Movie

interface StorageService {
    val all: Iterable<Movie>
    val tags: Iterable<String>
    fun save(movie: Movie)
}