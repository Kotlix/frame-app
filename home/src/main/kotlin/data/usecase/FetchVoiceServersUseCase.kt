package data.usecase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kotlix.frame.gateway.client.GatewayServerClient

class FetchVoiceServersUseCase(
    val api: GatewayServerClient
) {
    fun execute(
        token: String,
        callback: (Map<String, List<String>>?, error: String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.getServers(token)

                if (response.isSuccessful && response.body() != null) {
                    val resp = response.body()
                    callback(resp!!, null)
                } else {
                    callback(null, "Error ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                callback(null, "Exception: ${e.message}")
            }
        }
    }
}