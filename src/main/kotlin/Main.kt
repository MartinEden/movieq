package eden.movieq

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import eden.movieq.services.ImdbService
import eden.movieq.services.RottenTomatoesService
import eden.movieq.services.SqliteStorageService
import eden.movieq.services.TheMovieDbService
import java.io.File

fun main(args: Array<String>) {
    val config = Config.load("config.json")
    MovieQCommand(config).subcommands(MigrateCommand()).main(args)
}

class MovieQCommand(val config: Config): CliktCommand("movieq") {
    override val invokeWithoutSubcommand = true

    override fun run() {
        if (currentContext.invokedSubcommand == null) {
            MovieQApp(
                movieService = TheMovieDbService(token = config.theMovieDbToken),
                rottenTomatoesService = RottenTomatoesService(),
                store = SqliteStorageService()
            ).run()
        }
    }
}
