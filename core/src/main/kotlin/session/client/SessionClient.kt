package session.client

import session.client.exception.ConnectionFailedException
import kotlin.jvm.Throws

interface SessionClient {
    @Throws(ConnectionFailedException::class)
    suspend fun connectAuthorize(token: String)

    fun isConnected(): Boolean

    fun disconnect()

    suspend fun updateMessageNotificationPreferences(communityId: List<Long>): Boolean
}