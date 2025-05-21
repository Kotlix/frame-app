package data.usecase.userstate

import data.model.response.UserStateEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import ru.kotlix.frame.gateway.client.GatewayUserStateClient

class GetUserStateUseCase(
    private val api: GatewayUserStateClient
) {
    val logger = LoggerFactory.getLogger(this::class.java)
    fun execute(
        token: String,
        userId: Long,
        callback: (userState: UserStateEntity?, error: String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.getUserStatus(token, userId)

                if (response.isSuccessful) {
                    val resp = response.body()!!
                    val result = UserStateEntity(
                        resp.userId,
                        resp.online,
                        resp.lastActive
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