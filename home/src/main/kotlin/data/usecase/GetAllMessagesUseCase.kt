package data.usecase

import data.MessageApi
import dto.ChatEntity
import dto.MessageEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GetAllMessagesUseCase(
    val api: MessageApi
) {
    fun execute(
        token: String,
        chatId: Long,
        page: Long,
        size: Long,
        callback: (data: List<MessageEntity>?, error: String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.getMessages(chatId, page, size, token)

                if (response.isSuccessful) {
                    val resp = response.body()!!
                    val result = resp.map {
                        MessageEntity(
                            it.id,
                            it.chatId,
                            it.authorId,
                            it.message,
                            it.createdAt
                        )
                    }
                    callback(result, null)
                } else {
                    callback(null, "Error ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                callback(null, "Exception: ${e.message}")
            }
        }
    }
}