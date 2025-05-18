package session.client.handler

import ru.kotlix.frame.session.api.proto.SessionContract

interface ServerPacketListenerWatcher {
    fun shouldContinue(pkt: SessionContract.ServerPacket): Boolean

    companion object {
        val Once = object : ServerPacketListenerWatcher {
            override fun shouldContinue(pkt: SessionContract.ServerPacket): Boolean = false
        }

        val Forever = object : ServerPacketListenerWatcher {
            override fun shouldContinue(pkt: SessionContract.ServerPacket): Boolean = true
        }
    }

    class Times(
        private val count: Int
    ) : ServerPacketListenerWatcher {
        private var done = 0
        override fun shouldContinue(pkt: SessionContract.ServerPacket): Boolean = ++done < count
    }
}