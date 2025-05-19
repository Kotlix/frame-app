package data.usecase

import data.model.response.ProfileInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import ru.kotlix.frame.gateway.client.GatewayProfileClient

class GetProfileInfoUseCase(
    private val api: GatewayProfileClient
) {
    val logger = LoggerFactory.getLogger(this::class.java)
    fun execute(
        token: String,
        userId: Long,
        callback: (username: String?, error: String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.getProfileInfo(token, userId)

                if (response.isSuccessful) {
                    val resp = response.body()!!.username
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