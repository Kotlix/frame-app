package data.usecase

import dto.DirectoryEntity
import dto.VoiceEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kotlix.frame.gateway.api.dto.entities.GatewayVoiceDto
import ru.kotlix.frame.gateway.client.GatewayVoiceClient

class GetAllVoicesUseCase(
    val api: GatewayVoiceClient
) {

    fun execute(
        token: String,
        communityId: Long,
        callback: (data: List<VoiceEntity>?, error: String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.getAllVoices(token, communityId)

                if (response.isSuccessful) {
                    val resp = response.body()!!
                    val result = resp.map {
                        VoiceEntity(
                            it.id,
                            it.communityId,
                            it.name,
                            it.directoryId,
                            it.order
                        )
                    }
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