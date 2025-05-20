package data.usecase

import data.ChatApi
import data.model.request.CreateChatRequest
import data.model.response.ChatDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kotlix.frame.gateway.api.dto.entities.GatewayChatDto
import ru.kotlix.frame.gateway.api.dto.requests.GatewayCreateChatRequest
import ru.kotlix.frame.gateway.client.GatewayChatClient

class CreateChatUseCase(
    val api: GatewayChatClient
) {
    fun execute(
        token: String,
        communityId: Long,
        name: String,
        directoryId: Long,
        order: Int,
        callback: (data: GatewayChatDto?, error: String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.createChat(token, communityId, GatewayCreateChatRequest(
                        name,
                        directoryId,
                        order
                    )
                )

                if (response.isSuccessful) {
                    val resp = response.body()
                    callback(resp, null)
                } else {
                    callback(null, "Error ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                callback(null, "Exception: ${e.message}")
            }
        }
    }
}