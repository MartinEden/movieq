package eden.movieq.models

data class MovieShortDetails(
    val id: String,
    val title: String,
    val imageUrl: String?,
    val startYear: Int?
)