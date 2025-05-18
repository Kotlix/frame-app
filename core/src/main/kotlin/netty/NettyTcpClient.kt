package netty

import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.EventLoopGroup
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import org.slf4j.LoggerFactory
import ru.kotlix.frame.session.api.proto.SessionContract
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class NettyTcpClient(
    private val host: String,
    private val port: Int,
    private val sessionPacketHandler: SimpleChannelInboundHandler<SessionContract.ServerPacket>
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private lateinit var workerGroup: EventLoopGroup
    lateinit var channel: Channel
        private set

    suspend fun connect(): Boolean {
        workerGroup = NioEventLoopGroup()

        return try {
            channel = start(workerGroup, host, port)
            logger.info("Netty server connected.")
            true
        } catch (ex: Exception) {
            logger.error("Error during netty connect", ex)
            disconnect()
            false
        }
    }

    fun disconnect() {
        if (::workerGroup.isInitialized) {
            workerGroup.shutdownGracefully()
            logger.info("Netty disconnected from server.")
        }
    }

    private suspend fun start(
        group: EventLoopGroup,
        host: String,
        port: Int
    ): Channel = suspendCoroutine { cont ->
        try {
            val boostrap = Bootstrap()
                .group(group)
                .channel(NioSocketChannel::class.java)
                .handler(SocketChannelPipeline(sessionPacketHandler))

            val chFuture = boostrap.connect(host, port)

            chFuture.addListener {
                if (it.isSuccess) {
                    cont.resume(chFuture.channel())
                } else {
                    cont.resumeWithException(RuntimeException(it.cause()))
                }
            }
        } catch (ex: Exception) {
            cont.resumeWithException(ex)
        }
    }
}