package data.usecase

import data.ChatApi
import data.DirectoryApi
import data.model.request.CreateChatRequest
import data.model.request.CreateDirectoryRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kotlix.frame.gateway.api.dto.requests.GatewayCreateDirectoryRequest
import ru.kotlix.frame.gateway.client.GatewayDirectoryClient

class CreateDirectoryUseCase (
    val api: GatewayDirectoryClient
) {
    fun execute(
        token: String,
        communityId: Long,
        name: String,
        directoryId: Long?,
        order: Int,
        callback: (error: String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.createDirectory(token, communityId, GatewayCreateDirectoryRequest(
                    name,
                    directoryId,
                    order
                ))

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