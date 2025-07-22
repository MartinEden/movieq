package eden.movieq

import eden.movieq.services.ImdbService
import gg.jte.ContentType
import gg.jte.TemplateEngine
import gg.jte.resolve.ResourceCodeResolver
import io.javalin.Javalin
import io.javalin.http.Context
import io.javalin.http.staticfiles.Location
import io.javalin.rendering.template.JavalinJte

class MovieQApp(val imdbService: ImdbService) {

    fun run() {
        val app = Javalin.create {
            val templateResolver = ResourceCodeResolver("templates", MovieQApp::class.java.classLoader)
            it.fileRenderer(JavalinJte(TemplateEngine.create(templateResolver, ContentType.Html)))

            it.staticFiles.add { staticFiles ->
                staticFiles.hostedPath = "/static"
                staticFiles.directory = "static"
                staticFiles.location = Location.CLASSPATH
            }
        }
        app.get("") { it.render("index.kte") }
        app.get("lookup", ::lookupHandler)
        app.start(8080)
    }

    fun lookupHandler(ctx: Context) {
        val query = ctx.queryParam("query") ?: throw Exception("No query was provided")
        val titles = imdbService.search(query)
        ctx.render("lookup.kte", mapOf("query" to query, "titles" to titles))
    }
}