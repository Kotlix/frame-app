package data.model.response

data class DirectoryDto(
    val id: Long,
    val communityId: Long,
    val name: String,
    val directoryId: Long?,
    val order: Int
)
