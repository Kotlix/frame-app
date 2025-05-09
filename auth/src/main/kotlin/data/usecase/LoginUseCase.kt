package data.usecase

import data.api.AuthApi
import data.model.BasicLoginRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginUseCase(
    private val authApi: AuthApi
) {
    fun execute(
        login: String,
        password: String,
        callback: (token: String?, error: String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = authApi.login(BasicLoginRequest(login, password))

                if (response.isSuccessful) {
                    val token = response.body()!!
                    callback(token, response.toString())
                } else {
                    callback(null, response.toString())//"Error ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                callback(null, e.toString())//e.message ?: "Unknown error")
            }
        }
    }
}
