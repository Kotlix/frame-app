package dto

data class VoiceEntity(
    val id: Long,
    val communityId: Long,
    val name: String,
    val directoryId: Long,
    val order: Int
)
