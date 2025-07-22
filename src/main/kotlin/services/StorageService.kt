package eden.movieq.services

import eden.movieq.models.Movie

interface StorageService {
    val movies: Iterable<Movie>
}