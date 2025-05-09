package data.usecase

import data.model.request.CreateDirectoryRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kotlix.frame.gateway.api.dto.requests.GatewayCreateCommunityRequest
import ru.kotlix.frame.gateway.client.GatewayCommunityClient

class CreateCommunityUseCase(
    val api: GatewayCommunityClient
) {
    fun execute(
        token: String,
        name: String,
        desc: String?,
        isPublic: Long,
        name: Long,
        directory: CreateDirectoryRequest,
        callback: (error: String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.create(
                    GatewayCreateCommunityRequest(

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