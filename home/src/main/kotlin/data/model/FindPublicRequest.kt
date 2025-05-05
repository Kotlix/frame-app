package data.model

data class FindPublicRequest(
    val name: String,
    val pageOffset: Long,
    val pageCount: Long
)
