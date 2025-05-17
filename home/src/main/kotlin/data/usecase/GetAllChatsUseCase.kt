package data.usecase

import data.ChatApi
import data.MessageApi
import data.model.request.SendMessageRequest
import data.model.response.ChatDto
import dto.ChatEntity
import dto.CommunityEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kotlix.frame.gateway.api.GatewayChatApi
import ru.kotlix.frame.gateway.client.GatewayChatClient

class GetAllChatsUseCase(
    private val api: GatewayChatClient
) {
    fun execute(
        token: String,
        communityId: Long,
        callback: (data: List<ChatEntity>?, error: String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.getAllChats(token, communityId)

                if (response.isSuccessful) {
                    val resp = response.body()!!
                    val result = resp.map {
                        ChatEntity(
                            it.id,
                            it.communityId,
                            it.name,
                            it.directoryId,
                            it.order
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