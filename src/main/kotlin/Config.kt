package eden.movieq

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import kotlinx.serialization.json.decodeFromStream
import java.io.File

@Serializable
data class Config(
    val theMovieDbToken: String? = null
) {
    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        private val json = Json {
            namingStrategy = JsonNamingStrategy.SnakeCase
            ignoreUnknownKeys = true
        }

        @OptIn(ExperimentalSerializationApi::class)
        fun load(path: String): Config {
            val file = File(path)
            return if (file.exists()) {
                file.inputStream().use {
                    json.decodeFromStream(it)
                }
            } else {
                Config()
            }
        }
    }
}