package data.usecase.profile

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import ru.kotlix.frame.gateway.api.dto.requests.GatewayChangeUsernameRequest
import ru.kotlix.frame.gateway.client.GatewayProfileClient

class ChangeProfileUserNameUseCase(
    private val api: GatewayProfileClient
) {
    val logger = LoggerFactory.getLogger(this::class.java)
    fun execute(
        token: String,
        newUserName: String,
        callback: (error: String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.changeUsername(token, GatewayChangeUsernameRequest(
                        newUserName
                    )
                )

                if (response.isSuccessful) {
                    callback(null)
                } else {
                    callback( "Error ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                logger.error("myTag", e)

                callback("Exception: ${e.message}")
            }
        }
    }
}