package data.usecases

import data.model.ConnectionGuideEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kotlix.frame.gateway.api.dto.entities.GatewayConnectionGuide
import ru.kotlix.frame.gateway.api.dto.entities.GatewayVoiceDto
import ru.kotlix.frame.gateway.api.dto.requests.GatewayCreateVoiceRequest
import ru.kotlix.frame.gateway.client.GatewayVoiceClient

class JoinVoiceChatUseCase(
    val api: GatewayVoiceClient
) {
    fun execute(
        token: String,
        voiceId: Long,
        callback: (data: ConnectionGuideEntity?, error: String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.joinVoice(token, voiceId)

                if (response.isSuccessful) {
                    val resp = response.body()!!
                    val result = ConnectionGuideEntity(
                        resp.hostAddress,
                        resp.secret,
                        resp.channelId,
                        resp.shadowId
                    )
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