package eden.movieq.services

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import eden.movieq.MovieQApp
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking

class RottenTomatoesService(val endpointUrl: String = "https://www.rottentomatoes.com") {
    private val client: HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    fun getTomatoMeterFor(title: String, year: Int?): Int? {
        val url = getFilmURLFromSearch(title, year)
        if (url != null) {
            val html = getAndParse(url)
            val rawScore = html.select("media-scorecard rt-text[slot=critics-score]").text()
            val match = Regex("""(\d+)%""").find(rawScore)
            if (match != null) {
                return match.groupValues[1].toInt()
            } else {
                MovieQApp.logger.warn("Unable to retrieve RT score for '$title' from '$url'.")
                MovieQApp.logger.warn("Searching in '$rawScore' did not produce a match")
            }
        } else {
            MovieQApp.logger.warn("No result for '$title' in RottenTomatoes search")
        }
        return null
    }

    private fun getFilmURLFromSearch(title: String, year: Int?): String? {
        val searchPage = getAndParse("$endpointUrl/search") {
            url {
                parameter("search", title)
            }
        }
        val searchResult = searchPage
            .select("#search-results ul[slot=list] search-page-media-row")
            .asSequence()
            .map(::parseSearchRow)
            .firstOrNull { it.matches(title, year) }
        return searchResult?.url
    }

    private fun parseSearchRow(row: Element): MovieSearchResult {
        val titleAnchor = row.select("a[slot=title]")
        return MovieSearchResult(
            title = titleAnchor.text().trim(),
            year = getYearFromRow(row),
            url = titleAnchor.attr("href")
        )
    }

    private fun getYearFromRow(row: Element): Int? {
        val attributes = listOf("release-year", "start-year", "end-year")
        for (attributeName in attributes) {
            val year = row.attr(attributeName).toIntOrNull()
            if (year != null) {
                return year
            }
        }
        MovieQApp.logger.warn("Unable to find any year in RottenTomatoes search result: $row")
        return null
    }

    private fun getAndParse(urlString: String, block: HttpRequestBuilder.() -> Unit = {}): Document {
        val raw = runBlocking {
            client.get(urlString, block).bodyAsText()
        }
        return Ksoup.parse(raw)
    }
}

data class MovieSearchResult(val title: String, val year: Int?, val url: String) {
    fun matches(title: String, year: Int?): Boolean {
        return this.title == title
                && (year == null || this.year == year)
    }
}