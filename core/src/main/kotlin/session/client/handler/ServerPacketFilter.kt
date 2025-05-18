package session.client.handler

import ru.kotlix.frame.session.api.proto.SessionContract

interface ServerPacketFilter {
    fun shouldAccept(pkt: SessionContract.ServerPacket): Boolean

    companion object {
        val All = object : ServerPacketFilter {
            override fun shouldAccept(pkt: SessionContract.ServerPacket): Boolean = true
        }
    }

    class ServerResponseForPid(private val pid: Long) : ServerPacketFilter {
        override fun shouldAccept(pkt: SessionContract.ServerPacket): Boolean =
            pkt.hasServerResponse() && pkt.serverResponse.pid == pid
    }
}