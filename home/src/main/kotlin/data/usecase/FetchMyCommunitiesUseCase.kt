package data.usecase

import data.HomeApi
import dto.CommunityEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FetchMyCommunitiesUseCase(
    private val api: HomeApi
) {
    fun execute(
        token: String,
        callback: (data: List<CommunityEntity>?, error: String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.findMyCommunities(
                    token
                )

                if (response.isSuccessful) {
                    val resp = response.body()!!
                    val result = resp.data.map {
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