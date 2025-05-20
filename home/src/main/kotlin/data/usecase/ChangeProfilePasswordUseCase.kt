package data.usecase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import ru.kotlix.frame.gateway.api.dto.requests.GatewayChangeEmailRequest
import ru.kotlix.frame.gateway.api.dto.requests.GatewayChangePasswordRequest
import ru.kotlix.frame.gateway.client.GatewayProfileClient

class ChangeProfilePasswordUseCase(
    private val api: GatewayProfileClient
) {
    val logger = LoggerFactory.getLogger(this::class.java)
    fun execute(
        token: String,
        newPassword: String,
        callback: (error: String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.changePassword(token, GatewayChangePasswordRequest(
                        newPassword
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