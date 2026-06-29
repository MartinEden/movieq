package eden.movieq

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import eden.movieq.services.ImdbService
import eden.movieq.services.RottenTomatoesService
import eden.movieq.services.SqliteStorageService

fun main(args: Array<String>) {
    MovieQCommand().subcommands(MigrateCommand()).main(args)
}

class MovieQCommand: CliktCommand("movieq") {
    override val invokeWithoutSubcommand = true

    override fun run() {
        if (currentContext.invokedSubcommand == null) {
            MovieQApp(
                imdbService = ImdbService("https://api.imdbapi.dev"),
                rottenTomatoesService = RottenTomatoesService(),
                store = SqliteStorageService()
            ).run()
        }
    }
}
