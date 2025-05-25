package data.usecase.directory

import dto.DirectoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kotlix.frame.gateway.client.GatewayDirectoryClient

class GetAllDirectoriesUseCase(
    private val api: GatewayDirectoryClient
) {
    fun execute(
        token: String,
        communityId: Long,
        callback: (data: List<DirectoryEntity>?, error: String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.getAllDirectories(token, communityId)

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