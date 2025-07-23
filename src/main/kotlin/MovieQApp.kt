package eden.movieq

import eden.movieq.models.ImageInfo
import eden.movieq.models.Movie
import eden.movieq.services.ImdbService
import eden.movieq.services.StorageService
import gg.jte.ContentType
import gg.jte.TemplateEngine
import gg.jte.resolve.ResourceCodeResolver
import io.javalin.Javalin
import io.javalin.http.Context
import io.javalin.http.staticfiles.Location
import io.javalin.rendering.template.JavalinJte
import kotlinx.serialization.json.Json
import java.time.LocalDate

class MovieQApp(val imdbService: ImdbService, val store: StorageService) {
    private val serializer = Json { prettyPrint = true }

    fun run() {
        val app = makeJavalin()
        app.get("", ::indexHandler)
        app.get("lookup", ::lookupHandler)
        app.post("save", ::saveHandler)
        app.start(8080)
    }

    fun indexHandler(ctx: Context) {
        ctx.render("index.kte", mapOf(
            "movies" to store.all,
            "movieJson" to serializer.encodeToString(store.all.toList().toTypedArray())
        ))
    }

    fun lookupHandler(ctx: Context) {
        val query = ctx.queryParam("query") ?: throw Exception("No query was provided")
        val reason = ctx.queryParam("reason") ?: ""
        val titles = imdbService.search(query)
        ctx.render("lookup.kte", mapOf(
            "query" to query,
            "reason" to reason,
            "titles" to titles,
        ))
    }

    fun saveHandler(ctx: Context) {
        val id = ctx.formParam("movieId") ?: throw Exception("No movieId was provided")
        val reason = ctx.formParam("reason") ?: throw Exception("No reason was provided")
        val title = imdbService.get(id) ?: throw Exception("Couldn't find title $id in IMDB service")
        store.save(Movie(
            imdbId = id,
            reason = reason,
            title = title.primaryTitle,
            synopsis = "MISSING", // TODO: Synopsis
            year = title.startYear,
            dateAdded = LocalDate.now(),
            rating = title.rating?.aggregateRating?.times(10)?.toInt(),
            tomatoMeter = null, // TODO: Tomato Meter
            thumbnail = title.primaryImage?.url ?: ImageInfo.default.url, // TODO: Cache lower resolution version locally
            tags = listOf("tag1", "tag2"), // TODO: Tags
        ))
        ctx.redirect("/")
    }

    private fun makeJavalin(): Javalin = Javalin.create {
        val templateResolver = ResourceCodeResolver("templates", MovieQApp::class.java.classLoader)
        it.fileRenderer(JavalinJte(TemplateEngine.create(templateResolver, ContentType.Html)))

        it.staticFiles.add { staticFiles ->
            staticFiles.hostedPath = "/static"
            staticFiles.directory = "static"
            staticFiles.location = Location.CLASSPATH
            staticFiles.mimeTypes.add(io.javalin.http.ContentType.JAVASCRIPT, "static/js")
        }
    }
}