package data.usecase

import data.api.AuthApi
import data.model.BasicRegisterRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VerifyCodeUseCase (
    private val authApi: AuthApi
) {
    fun execute(
        code: String,
        callback: (error: String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = authApi.verifyRegister(code)

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