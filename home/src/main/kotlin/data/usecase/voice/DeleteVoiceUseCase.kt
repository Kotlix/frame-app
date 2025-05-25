package data.usecase.voice

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kotlix.frame.gateway.api.dto.entities.GatewayVoiceDto
import ru.kotlix.frame.gateway.api.dto.requests.GatewayCreateVoiceRequest
import ru.kotlix.frame.gateway.client.GatewayVoiceClient

class DeleteVoiceUseCase(
    val api: GatewayVoiceClient
) {
    fun execute(
        token: String,
        voiceId: Long,
        callback: (error: String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.deleteVoice(token, voiceId)

                if (response.isSuccessful) {
                    callback( null)
                } else {
                    callback( "Error ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                callback("Exception: ${e.message}")
            }
        }
    }
}