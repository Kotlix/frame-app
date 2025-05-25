package netty

import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.EventLoopGroup
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.DatagramPacket
import io.netty.channel.socket.nio.NioDatagramChannel
import org.slf4j.LoggerFactory
import ru.kotlix.frame.router.api.proto.RoutingContract
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class NettyUdpClient(
    private val voicePacketHandler: SimpleChannelInboundHandler<DatagramPacket>
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private lateinit var workerGroup: EventLoopGroup
    lateinit var channel: Channel
        private set

    suspend fun connect(): Boolean {
        workerGroup = NioEventLoopGroup()

        return try {
            channel = start(workerGroup)
            logger.info("Netty voice started.")
            true
        } catch (ex: Exception) {
            logger.error("Error during netty voice start.", ex)
            disconnect()
            false
        }
    }

    fun disconnect() {
        if (::workerGroup.isInitialized) {
            workerGroup.shutdownGracefully()
            logger.info("Netty voice shutdown.")
        }
    }

    private suspend fun start(
        group: EventLoopGroup,
    ): Channel = suspendCoroutine { cont ->
        try {
            val boostrap = Bootstrap()
                .group(group)
                .channel(NioDatagramChannel::class.java)
                .handler(DatagramChannelPipeline(voicePacketHandler))

            val chFuture = boostrap.bind(0)

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