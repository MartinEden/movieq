package eden.movieq.services

import eden.movieq.models.ImageInfo
import eden.movieq.models.Movie
import java.time.LocalDate

@Suppress("unused")
class DummyStorageService : StorageService {
    override fun save(movie: Movie) {
        movies.add(movie)
    }

    override val all get() = movies
    override val tags get() = movies.flatMap { it.tags }.distinct().sortedBy { it }

    private val movies: MutableList<Movie> = mutableListOf(
        Movie(
            imdbId = "dummy1",
            title = "A film about grass",
            dateAdded = LocalDate.of(2025, 5, 16),
            reason = "I love gardening, and it stars Liam Neeson",
            synopsis = "In a touching memorial to gardening, everyone touches grass. But all is not as it seems in the rock garden...",
            year = 2025,
            rating = 46,
            tomatoMeter = 76,
            thumbnail = ImageInfo.default.url,
            tags = listOf("drama", "romance", "horror")
        ),
        Movie(
            imdbId = "dummy2",
            title = "Excalibur 9",
            dateAdded = LocalDate.of(2022, 1, 31),
            reason = "Recommended by everyone on the /badmovies subreddit; seemed hilarious",
            synopsis = "Arthur is a businessman. One day he finds a massive sword and discovers his true destiny...",
            year = 1905,
            rating = 99,
            tomatoMeter = 11,
            thumbnail = ImageInfo.default.url,
            tags = listOf("fantasy", "action", "romance")
        )
    )
}