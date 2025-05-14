package data.model.request

data class UpdateChatRequest(
    val name: String,
    val directoryId: Long,
    val order: Int
)
