package eden.movieq.services

import eden.movieq.MovieQApp
import eden.movieq.models.imdb.ImageInfo
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.copyAndClose
import kotlinx.coroutines.runBlocking
import java.io.File

class ThumbnailClient(val client: HttpClient) {
    fun downloadThumbnailAndGetPath(imdbId: String, title: String, imageUrl: String?): String {
        return if (imageUrl != null) {
            runBlocking {
                val fileName = generateThumbnailFileName(imdbId, title)
                val file = File(MovieQApp.THUMBNAIL_DIRECTORY, fileName)
                client.get(imageUrl).bodyAsChannel().copyAndClose(file.writeChannel())
                MovieQApp.logger.info("Saved thumbnail to ${file.path}")
                "/static/thumbnails/$fileName"
            }
        } else {
            ImageInfo.default.url
        }
    }

    private fun generateThumbnailFileName(imdbId: String, title: String)
            = imdbId + "-" + title.lowercase().replace(Regex("""\W+"""), "-");
}