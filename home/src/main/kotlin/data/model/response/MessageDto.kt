package data.model.response

import java.time.LocalDateTime

data class MessageDto(
    val id: Long,
    val chatId: Long,
    val authorId: Long,
    val message: String,
    val createdAt: LocalDateTime
)
