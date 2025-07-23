package eden.movieq.models

import eden.movieq.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Movie(
    val imdbId: String,
    val reason: String,
    val title: String,
    val synopsis: String,
    val year: Int?,
    @Serializable(with = LocalDateSerializer::class)
    val dateAdded: LocalDate,
    // Out of 100
    val rating: Int? = null,
    // Out of 100
    val tomatoMeter: Int? = null,
    val thumbnail: String,
    val tags: List<String>
)
