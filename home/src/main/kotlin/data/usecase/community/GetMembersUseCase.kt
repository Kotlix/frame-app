package data.usecase.community

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import ru.kotlix.frame.gateway.client.GatewayCommunityClient

class GetMembersUseCase(
    private val api: GatewayCommunityClient
) {
    val logger = LoggerFactory.getLogger(this::class.java)
    fun execute(
        token: String,
        communityId: Long,
        callback: (username: List<Long>?, error: String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.getMembers(token, communityId)

                if (response.isSuccessful) {
                    val resp = response.body()!!.map {
                        it.userId
                    }
                    callback(resp, null)
                } else {
                    callback(null, "Error ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                logger.error("myTag", e)

                callback(null, "Exception: ${e.message}")
            }
        }
    }

}