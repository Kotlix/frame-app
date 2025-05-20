package data.usecase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import ru.kotlix.frame.gateway.client.GatewayProfileClient

class ChangeProfilePasswordApplyUseCase(
    private val api: GatewayProfileClient
) {
    val logger = LoggerFactory.getLogger(this::class.java)
    fun execute(
        token: String,
        secret: String,
        callback: (error: String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.changePasswordApply(token, secret)

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