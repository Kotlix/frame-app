package data.usecases

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kotlix.frame.gateway.client.GatewayVoiceClient

class LeaveVoiceUseCase(
    val api: GatewayVoiceClient
) {
    fun execute(
        token: String,
        voiceId: Long,
        callback: (error: String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.leaveVoice(token, voiceId)

                if (response.isSuccessful) {
                    val resp = response.body()
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