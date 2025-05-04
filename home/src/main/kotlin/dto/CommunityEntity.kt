package dto

data class CommunityEntity(
    val id: Long,
    val name: String,
    val description: String?,
    val isPublic: Boolean,
)