package eden.movieq

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
            "movies" to serializeList(store.all),
            "allTags" to serializeList(store.tags),
        ))
    }

    fun lookupHandler(ctx: Context) {
        val query = ctx.queryParam("query") ?: throw Exception("No query was provided")
        val maxResults = ctx.queryParam("maxResults")?.toInt() ?: 3
        val reason = ctx.queryParam("reason") ?: ""
        val titles = imdbService.search(query, maxResults)
        ctx.render("lookup.kte", mapOf(
            "query" to query,
            "reason" to reason,
            "maxResults" to maxResults,
            "titles" to titles,
        ))
    }

    fun saveHandler(ctx: Context) {
        val id = ctx.formParam("movieId") ?: throw Exception("No movieId was provided")
        val reason = ctx.formParam("reason") ?: throw Exception("No reason was provided")
        val movie = imdbService.get(id, reason)
        store.save(movie)
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

    private inline fun <reified T> serializeList(list: Iterable<T>) = serializer.encodeToString(list.toList().toTypedArray())

    companion object {
        val thumbnailPath: String = MovieQApp::class.java.classLoader.getResource("static/thumbnails")?.file
            ?: throw Exception("Unable to get thumbnail directory")
    }
}