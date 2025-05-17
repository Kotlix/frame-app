package data.usecase

import data.HomeApi
import dto.CommunityEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kotlix.frame.gateway.client.GatewayCommunityClient

class FetchCommunitiesUseCase(
    private val api: GatewayCommunityClient
) {
    fun execute(
        token: String,
        name: String,
        pageOffset: Long,
        pageCount: Long,
        callback: (data: List<CommunityEntity>?, error: String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.findAllPublicWithFilter(token,
                    name, pageOffset, pageCount)

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