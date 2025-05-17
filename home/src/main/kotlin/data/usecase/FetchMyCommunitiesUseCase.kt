package data.usecase

import data.HomeApi
import dto.CommunityEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kotlix.frame.gateway.client.GatewayCommunityClient
import ru.kotlix.frame.gateway.client.GatewayMessageClient

class FetchMyCommunitiesUseCase(
    private val api: GatewayCommunityClient,
    //private val mess: GatewayMessageClient
) {
    fun execute(
        token: String,
        callback: (data: List<CommunityEntity>?, error: String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.findAllMine(
                    token
                )

                //val res = mess.getMessages("0", 0, 0, 0)

                if (response.isSuccessful) {
                    val resp = response.body()!!
                    val result = resp.map {
                        CommunityEntity(
                            it.id,
                            it.name,
                            it.description,
                            it.isPublic
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