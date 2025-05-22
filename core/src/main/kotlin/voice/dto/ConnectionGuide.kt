package voice.dto

data class ConnectionGuide(
    val host: String,
    val port: Int,
    val channelId: Long,
    val shadowId: Int
)
