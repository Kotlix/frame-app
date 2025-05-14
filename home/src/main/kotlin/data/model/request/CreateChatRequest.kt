package data.model.request

data class CreateChatRequest(
    val name: String,
    val directoryId: Long,
    val order: Int,
)
