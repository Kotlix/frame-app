package data.model.response

data class UserStateEntity(
    val userId: Long,
    val online: Boolean,
    val lastActive: java.time.OffsetDateTime
)
