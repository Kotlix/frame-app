package data.usecase

import data.MessageApi
import dto.ChatEntity
import dto.MessageEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import ru.kotlix.frame.gateway.client.GatewayMessageClient

class GetAllMessagesUseCase(
    val api: GatewayMessageClient
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    fun execute(
        token: String,
        chatId: Long,
        page: Long,
        size: Long,
        callback: (data: List<MessageEntity>?, error: String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                logger.info(api.toString())
                val response = api.getMessages(token, chatId, page, size)
                logger.info("9823rj9u2f9u29")

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
                    logger.info("ERROR")
                    callback(null, "Error ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                logger.error("myTag", e)

                callback(null, "Exception: ${e.message}")
            }
        }
    }
}