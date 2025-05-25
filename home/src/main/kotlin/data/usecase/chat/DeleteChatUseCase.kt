package data.usecase.chat

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kotlix.frame.gateway.api.dto.entities.GatewayChatDto
import ru.kotlix.frame.gateway.api.dto.requests.GatewayCreateChatRequest
import ru.kotlix.frame.gateway.client.GatewayChatClient

class DeleteChatUseCase(
    val api: GatewayChatClient
) {
    fun execute(
        token: String,
        chatId: Long,
        callback: (error: String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.deleteChat(token, chatId)

                if (response.isSuccessful) {
                    callback(null)
                } else {
                    callback( "Error ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                callback( "Exception: ${e.message}")
            }
        }
    }
}