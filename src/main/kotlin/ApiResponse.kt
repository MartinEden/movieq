package eden.movieq

import kotlinx.serialization.Serializable

@Serializable
enum class ApiResult {
    Success,
    Error
}

@Serializable
data class ApiResponse(val result: ApiResult, val message: String) {
    companion object {
        val good = ApiResponse(ApiResult.Success, "OK")
        fun error(message: String) = ApiResponse(ApiResult.Error, message)
    }
}