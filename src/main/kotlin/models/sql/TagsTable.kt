package eden.movieq.models.sql

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

object TagsTable : IntIdTable("tags") {
    val name = varchar("name", 128)
}

class TagEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<TagEntity>(TagsTable)

    var name by TagsTable.name
}

object MovieTagsTable : Table("movie_tags") {
    val movie = reference("movie", MoviesTable)
    val tag = reference("tag", TagsTable)
    override val primaryKey = PrimaryKey(movie, tag, name = "PK_movie_tag")
}