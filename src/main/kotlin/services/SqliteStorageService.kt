package eden.movieq.services

import eden.movieq.models.Movie
import eden.movieq.models.sql.*
import org.jetbrains.exposed.v1.core.StdOutSqlLogger
import org.jetbrains.exposed.v1.dao.with
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.SizedCollection
import org.jetbrains.exposed.v1.jdbc.addLogger
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.sql.Connection

class SqliteStorageService : StorageService {
    init {
        TransactionManager.defaultDatabase = Database.connect("jdbc:sqlite:.movieq.db", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        transaction {
            SchemaUtils.create(MoviesTable)
            SchemaUtils.create(TagsTable)
            SchemaUtils.create(MovieTagsTable)
        }
    }

    override val all get() = transaction {
        addLogger(StdOutSqlLogger)
        MovieEntity.all().with(MovieEntity::tags).toList().map { it.toMovie() }
    }
    override val tags get() = transaction {
        addLogger(StdOutSqlLogger)
        TagEntity.all().toList().map { it.name }
    }

    override fun save(movie: Movie) {
        transaction {
            addLogger(StdOutSqlLogger)
            val tagLookup = TagEntity.all().associateBy { it.name }.toMutableMap()
            for (tag in movie.tags) {
                if (tag !in tagLookup) {
                    tagLookup[tag] = TagEntity.new { name = tag }
                }
            }

            MovieEntity.new {
                imdbId = movie.imdbId
                reason = movie.reason
                title = movie.title
                synopsis = movie.synopsis
                year = movie.year
                dateAdded = movie.dateAdded
                rating = movie.rating
                tomatoMeter = movie.tomatoMeter
                thumbnail = movie.thumbnail
                tags = SizedCollection(tagLookup.filterKeys { it in movie.tags }.map { it.value })
            }
        }
    }
}