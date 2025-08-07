package eden.movieq.models.sql

import eden.movieq.models.Movie
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.javatime.date

const val MAX_VARCHAR_LENGTH = 128

object MoviesTable : IntIdTable("movies") {
    val imdbId = varchar("imdbId", MAX_VARCHAR_LENGTH).uniqueIndex()
    val reason = text("reason")
    val title = varchar("title", MAX_VARCHAR_LENGTH)
    val synopsis = text("synopsis")
    val year = integer("year").nullable()
    val dateAdded = date("dateAdded")
    val rating = integer("rating").nullable()
    val tomatoMeter = integer("tomatoMeter").nullable()
    val thumbnail = varchar("thumbnail", MAX_VARCHAR_LENGTH)
}

class MovieEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MovieEntity>(MoviesTable)

    var imdbId by MoviesTable.imdbId
    var reason by MoviesTable.reason
    var title by MoviesTable.title
    var synopsis by MoviesTable.synopsis
    var year by MoviesTable.year
    var dateAdded by MoviesTable.dateAdded
    var rating by MoviesTable.rating
    var tomatoMeter by MoviesTable.tomatoMeter
    var thumbnail by MoviesTable.thumbnail
    var tags by TagEntity via MovieTagsTable

    fun toMovie() = Movie(
        imdbId,
        reason,
        title,
        synopsis,
        year,
        dateAdded,
        rating,
        tomatoMeter,
        thumbnail,
        tags.toList().map { it.name })
}

