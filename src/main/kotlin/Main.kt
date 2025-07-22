package eden.movieq

import eden.movieq.services.ImdbService

fun main() {
    MovieQApp(
        imdbService = ImdbService("https://api.imdbapi.dev")
    ).run()
}