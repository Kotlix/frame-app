package data.model.request

data class UpdateDirectoryRequest(
    val name: String,
    val directoryId: Long?,
    val order: Int
)
