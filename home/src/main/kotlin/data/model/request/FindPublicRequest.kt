package data.model.request

data class FindPublicRequest(
    val name: String,
    val pageOffset: Long,
    val pageCount: Long
)
