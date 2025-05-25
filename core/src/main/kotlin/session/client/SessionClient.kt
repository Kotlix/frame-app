package session.client

import exception.ConnectionFailedException
import session.client.handler.ServerPacketListenerRegistry
import kotlin.jvm.Throws

interface SessionClient {
    @Throws(ConnectionFailedException::class)
    suspend fun connectAuthorize(token: String)

    fun isConnected(): Boolean

    fun disconnect()

    fun getPacketListener(): ServerPacketListenerRegistry

    suspend fun updateMessageNotificationPreferences(communityId: List<Long>): Boolean
}