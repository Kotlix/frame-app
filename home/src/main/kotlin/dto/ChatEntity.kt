package dto

data class ChatEntity (
    val id: Long,
    val communityId: Long,
    val name: String,
    val directoryId: Long,
    val order: Int
)