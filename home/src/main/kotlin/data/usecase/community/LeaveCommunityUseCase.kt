package data.usecase.community

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kotlix.frame.gateway.client.GatewayCommunityClient

class LeaveCommunityUseCase(
    private val api: GatewayCommunityClient
) {
    fun execute(
        token: String,
        communityId: Long,
        callback: (error: String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.leaveCommunity( token, communityId)

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