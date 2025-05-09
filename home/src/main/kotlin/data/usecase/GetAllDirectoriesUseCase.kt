package data.usecase

import data.ChatApi
import data.DirectoryApi
import dto.ChatEntity
import dto.DirectoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GetAllDirectoriesUseCase(
    private val api: DirectoryApi
) {
    fun execute(
        token: String,
        communityId: Long,
        callback: (data: List<DirectoryEntity>?, error: String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.getAllDirectories(communityId, token)

                if (response.isSuccessful) {
                    val resp = response.body()!!
                    val result = resp.map {
                        DirectoryEntity(
                            it.id,
                            it.communityId,
                            it.name,
                            it.directoryId,
                            it.order
                        )
                    }
                    callback(result, null)
                } else {
                    callback(null, "Error ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                callback(null, "Exception: ${e.message}")
            }
        }
    }

}