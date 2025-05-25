package data.usecase.directory

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kotlix.frame.gateway.api.dto.requests.GatewayCreateDirectoryRequest
import ru.kotlix.frame.gateway.api.dto.requests.GatewayUpdateDirectoryRequest
import ru.kotlix.frame.gateway.client.GatewayDirectoryClient

class UpdateDirectoryUseCase  (
    val api: GatewayDirectoryClient
) {
    fun execute(
        token: String,
        id: Long,
        name: String,
        directoryId: Long?,
        order: Int,
        callback: (error: String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.updateDirectory(token, id, GatewayUpdateDirectoryRequest(
                    name,
                    directoryId,
                    order
                )
                )

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