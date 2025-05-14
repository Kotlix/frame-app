package data.usecase

import data.ChatApi
import data.DirectoryApi
import data.model.request.CreateChatRequest
import data.model.request.CreateDirectoryRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateDirectoryUseCase (
    val api: DirectoryApi
) {
    fun execute(
        token: String,
        communityId: Long,
        directory: CreateDirectoryRequest,
        callback: (error: String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.createDirectory(communityId, directory, token)

                if (response.isSuccessful) {
                    callback(null)
                } else {
                    callback("Error ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                callback("Exception: ${e.message}")
            }
        }
    }
}