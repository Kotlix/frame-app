package data.usecase.voice

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kotlix.frame.gateway.api.dto.entities.GatewayVoiceDto
import ru.kotlix.frame.gateway.api.dto.requests.GatewayCreateVoiceRequest
import ru.kotlix.frame.gateway.api.dto.requests.GatewayUpdateVoiceRequest
import ru.kotlix.frame.gateway.client.GatewayVoiceClient

class UpdateVoiceUseCase (
    val api: GatewayVoiceClient
) {
    fun execute(
        token: String,
        voiceId: Long,
        name: String,
        directoryId: Long,
        order: Int,
        callback: (data: GatewayVoiceDto?, error: String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.updateVoice(token, voiceId, GatewayUpdateVoiceRequest(
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