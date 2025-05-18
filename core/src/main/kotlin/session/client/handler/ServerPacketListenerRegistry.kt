package session.client.handler

import ru.kotlix.frame.session.api.proto.SessionContract

interface ServerPacketListenerRegistry {
    fun register(
        filter: ServerPacketFilter,
        watcher: ServerPacketListenerWatcher,
        listener: (SessionContract.ServerPacket?) -> Unit
    ): Long

    fun remove(id: Long)
}