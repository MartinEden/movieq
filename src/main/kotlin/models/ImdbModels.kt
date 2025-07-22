package eden.movieq.models

import kotlinx.serialization.Serializable

@Serializable
data class SearchResult(val titles: List<TitleInfo>)
@Serializable
data class TitleInfo(
    val id: String,
    val type: String,
    val primaryTitle: String,
    val originalTitle: String,
    val primaryImage: ImageInfo = ImageInfo("/static/no-image.png", 300, 200),
    val startYear: Int? = null,
    val endYear: Int? = null,
    val rating: RatingInfo? = null,
)
@Serializable
data class ImageInfo(val url: String, val width: Int, val height: Int)
@Serializable
data class RatingInfo(val aggregateRating: Double, val voteCount: Long)
