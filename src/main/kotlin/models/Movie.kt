package eden.movieq.models

import java.time.LocalDate

data class Movie(
    val imdbId: String,
    val reason: String,
    val title: String,
    val synopsis: String,
    val year: Int? = null,
    val dateAdded: LocalDate,
    // Out of 100
    val rating: Int,
    // Out of 100
    val tomatoMeter: Int,
    val thumbnail: String,
    val tags: List<String>
)
