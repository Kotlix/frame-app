package data.model.response

data class VoiceDto(
    val id: Long,
    val communityId: Long,
    val name: String,
    val directoryId: Long,
    val order: Int
)
