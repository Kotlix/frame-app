package data.usecase.profile

import data.model.response.ProfileInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import ru.kotlix.frame.gateway.client.GatewayProfileClient

class GetMyProfileInfo(
    private val api: GatewayProfileClient
) {
    val logger = LoggerFactory.getLogger(this::class.java)
    fun execute(
        token: String,
        callback: (data: ProfileInfo?, error: String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.getMyProfileInfo(token)

                if (response.isSuccessful) {
                    val resp = response.body()!!
                    val result =
                        ProfileInfo(
                            resp.id,
                            resp.login,
                            resp.username,
                            resp.email
                        )
                    callback(result, null)
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