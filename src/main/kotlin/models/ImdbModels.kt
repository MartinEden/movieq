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
    val primaryImage: ImageInfo = ImageInfo.default,
    val startYear: Int? = null,
    val endYear: Int? = null,
    val rating: RatingInfo? = null,
)
@Serializable
data class ImageInfo(val url: String, val width: Int, val height: Int, val type: String? = null) {
    companion object {
        val default = ImageInfo("/static/no-image.png", 300, 200)
    }
}
@Serializable
data class RatingInfo(val aggregateRating: Double, val voteCount: Long)

@Serializable
data class FullTitleInfo(
    val id: String,
    val type: String,
    val isAdult: Boolean? = false,
    val primaryTitle: String,
    val originalTitle: String? = null,
    val primaryImage: ImageInfo? = null,
    val startYear: Int? = null,
    val endYear: Int? = null,
    val runtimeSeconds: Int? = null,
    val genres: List<String> = emptyList(),
    val rating: RatingInfo? = null,
    val metacritic: MetacriticInfo? = null,
    val plot: String? = null,
    val originCountries: List<Country> = emptyList(),
    val spokenLanguages: List<Country> = emptyList(),
    val directors: List<Person> = emptyList(),
    val writers: List<Person> = emptyList(),
    val stars: List<Person> = emptyList()
)
@Serializable
data class MetacriticInfo(
    val url: String? = null,
    val score: Int? = null,
    val reviewCount: Int? = null
)
@Serializable
data class Country(val code: String? = null, val name: String? = null)

@Serializable
data class ImageSearchResult(
    val images: List<ImageInfo>,
    val totalCount: Int,
    val nextPageToken: String? = null
)

@Serializable
data class Person(
    val id: String,
    val displayName: String? = null,
    val primaryImage: ImageInfo? = null,
    val alternativeNames: List<String> = emptyList(),
    val primaryProfessions: List<String> = emptyList()
)
