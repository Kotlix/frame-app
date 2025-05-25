package data.model

data class ConnectionGuideEntity(
    val hostAddress: String,
    val secret: String,
    val channelId: Long,
    val shadowId: Int
)