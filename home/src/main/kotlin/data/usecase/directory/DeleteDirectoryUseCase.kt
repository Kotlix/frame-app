package data.usecase.directory

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kotlix.frame.gateway.api.dto.requests.GatewayCreateDirectoryRequest
import ru.kotlix.frame.gateway.client.GatewayChatClient
import ru.kotlix.frame.gateway.client.GatewayDirectoryClient

class DeleteDirectoryUseCase (
    val api: GatewayDirectoryClient
) {
    fun execute(
        token: String,
        directoryId: Long,
        callback: (error: String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.deleteDirectory(token, directoryId)

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