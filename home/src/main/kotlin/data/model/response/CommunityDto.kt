package data.model.response

data class FindPublicResponse(
    val data: List<CommunityDto>
) {
    class CommunityDto(
        val id: Long,
        val name: String,
        val description: String?,
        val isPublic: Boolean,
    )
}