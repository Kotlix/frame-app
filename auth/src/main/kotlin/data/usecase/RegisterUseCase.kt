package data.usecase

import data.api.AuthApi
import data.model.BasicLoginRequest
import data.model.BasicRegisterRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterUseCase(
    private val authApi: AuthApi
) {
    fun execute(
        login: String,
        password: String,
        username: String,
        email: String,
        callback: (error: String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = authApi.register(BasicRegisterRequest(login, password, username, email))

                if (response.isSuccessful) {
                    callback(null)
                } else {
                    callback("Error ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                callback( e.message ?: "Unknown error")
            }
        }
    }
}