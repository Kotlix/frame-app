package data.usecase

import data.ChatApi
import data.model.request.CreateChatRequest
import data.model.response.ChatDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateChatUseCase(
    val api: ChatApi
) {
    fun execute(
        token: String,
        communityId: Long,
        chat: CreateChatRequest,
        callback: (error: String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.createChat(communityId, chat, token)

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