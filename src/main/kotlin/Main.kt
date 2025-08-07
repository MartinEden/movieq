package eden.movieq

import eden.movieq.services.ImdbService
import eden.movieq.services.RottenTomatoesService
import eden.movieq.services.SqliteStorageService

fun main() {
    MovieQApp(
        imdbService = ImdbService("https://api.imdbapi.dev"),
        rottenTomatoesService = RottenTomatoesService(),
        store = SqliteStorageService()
    ).run()
}