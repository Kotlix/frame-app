package data.usecase

import data.HomeApi
import data.MessageApi
import data.model.request.SendMessageRequest
import data.model.response.MessageDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SendMessageUseCase(
    private val api: MessageApi
) {
    fun execute(
        token: String,
        chatId: Long,
        messageDto: SendMessageRequest,
        callback: (error: String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.sendMessage( token, chatId, messageDto)

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