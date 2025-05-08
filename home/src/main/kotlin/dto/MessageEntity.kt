package dto

import java.time.LocalDateTime

data class MessageEntity(
    val id: Long,
    val chatId: Long,
    val authorId: Long,
    val message: String,
    val createdAt: LocalDateTime
)
