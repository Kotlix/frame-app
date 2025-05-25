package data.usecase.message

import data.model.request.SendMessageRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import ru.kotlix.frame.gateway.api.dto.requests.GatewaySendMessageRequest
import ru.kotlix.frame.gateway.client.GatewayMessageClient

class SendMessageUseCase(
    private val api: GatewayMessageClient
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    fun execute(
        token: String,
        chatId: Long,
        messageDto: SendMessageRequest,
        callback: (error: String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                logger.info(messageDto.message)
                val response = api.sendMessage( token, chatId,
                    GatewaySendMessageRequest(
                        messageDto.message
                    )
                )
                logger.info("///////")


                if (response.isSuccessful) {
                    callback(null)
                } else {
                    callback("Error ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                callback("Exception: ${e.message}")
            }
        }
    }

}