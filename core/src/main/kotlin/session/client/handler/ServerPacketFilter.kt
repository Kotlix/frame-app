package session.client.handler

import ru.kotlix.frame.session.api.proto.SessionContract

fun interface ServerPacketFilter {
    fun shouldAccept(pkt: SessionContract.ServerPacket): Boolean

    companion object {
        val All = ServerPacketFilter { true }
    }

    class ServerResponseForPid(private val pid: Long) : ServerPacketFilter {
        override fun shouldAccept(pkt: SessionContract.ServerPacket): Boolean =
            pkt.hasServerResponse() && pkt.serverResponse.pid == pid
    }
}