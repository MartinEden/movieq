package eden.movieq.models.themoviedb

import kotlinx.serialization.Serializable

@Serializable
data class MovieDetails(
    val id: Int,
    val imdbId: String,
    val title: String,
    val overview: String,
    val releaseDate: String?,
    val posterPath: String?,
    val genres: List<Genre>,
    val voteAverage: Double
)

@Serializable
data class Genre(
    val id: Int,
    val name: String
)