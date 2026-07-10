package eden.movieq.models.themoviedb

import kotlinx.serialization.Serializable

@Serializable
data class SearchResultCollection(
    val page: Int,
    val results: List<SearchResult>,
    val totalPages: Int,
    val totalResults: Int,
)

@Serializable
data class SearchResult(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val releaseDate: String,
)