package data.model.request

data class CreateDirectoryRequest(
    val name: String,
    val directoryId: Long?,
    val order: Int
)
