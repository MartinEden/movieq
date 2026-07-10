package eden.movieq.viewModels

import eden.movieq.models.MovieShortDetails

data class LookupViewModel(
    val query: String,
    val reason: String,
    val moreResults: Int,
    val moreResultsAvailable: Boolean,
    val titles: List<MovieShortDetails>
)