package session.client.handler

import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import ru.kotlix.frame.session.api.proto.SessionContract
import java.util.concurrent.ConcurrentHashMap

@Sharable
class ServerPacketListenerRegistryImpl : SimpleChannelInboundHandler<SessionContract.ServerPacket>(),
    ServerPacketListenerRegistry {
    data class ListenerEntry(
        val filter: ServerPacketFilter,
        val watcher: ServerPacketListenerWatcher,
        val listener: (SessionContract.ServerPacket?) -> Unit
    )

    private var newKey: Long = 0
        get() {
            return field++
        }

    private val listeners = ConcurrentHashMap<Long, ListenerEntry>()

    override fun register(
        filter: ServerPacketFilter,
        watcher: ServerPacketListenerWatcher,
        listener: (SessionContract.ServerPacket?) -> Unit
    ): Long {
        val key = newKey

        listeners[key] = ListenerEntry(filter, watcher, listener)
        return key
    }

    override fun remove(id: Long) {
        listeners.remove(id)
    }

    override fun channelRead0(ctx: ChannelHandlerContext?, pkt: SessionContract.ServerPacket?) {
        val packet = pkt ?: return

        listeners.filter { (_, value) -> value.filter.shouldAccept(packet) }.toList()
            .forEach { (k, v) ->
                v.listener(packet)
                if (!v.watcher.shouldContinue(packet)) {
                    listeners.remove(k)
                }
            }
    }

    override fun channelInactive(ctx: ChannelHandlerContext?) {
        listeners.forEach { (_, v) ->
            v.listener(null)
        }
    }
}
