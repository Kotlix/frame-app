package data.usecase

import data.model.request.CreateDirectoryRequest
import dto.CommunityEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kotlix.frame.gateway.api.dto.entities.GatewayCommunityDto
import ru.kotlix.frame.gateway.api.dto.requests.GatewayCreateCommunityRequest
import ru.kotlix.frame.gateway.client.GatewayCommunityClient
import ru.kotlix.frame.gateway.client.GatewayMessageClient

class CreateCommunityUseCase(
    val api: GatewayCommunityClient
) {
    fun execute(
        token: String,
        name: String,
        desc: String?,
        isPublic: Boolean,
        voiceRegion: String,
        voiceName: String,
        callback: (community: CommunityEntity?, error: String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.create(token, GatewayCreateCommunityRequest(
                        name,
                        desc,
                        isPublic,
                        voiceRegion,
                        voiceName
                    )
                )

                if (response.isSuccessful && response.body() != null) {
                    val resp = response.body()
                    val res = CommunityEntity(
                        resp!!.id,
                        resp.name,
                        resp.description,
                        resp.isPublic
                    )
                    callback(res, null)
                } else {
                    callback(null, "Error ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                callback(null, "Exception: ${e.message}")
            }
        }
    }
}