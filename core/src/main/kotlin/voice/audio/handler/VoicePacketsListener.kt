package voice.audio.handler

import com.google.protobuf.InvalidProtocolBufferException
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.socket.DatagramPacket
import org.slf4j.LoggerFactory
import ru.kotlix.frame.router.api.proto.RoutingContract
import voice.VoiceManager
import voice.audio.mixing.AudioMixer
import voice.audio.mixing.OrderedPacket
import voice.audio.security.AesBytesDecoder
import java.util.concurrent.ConcurrentMap

class VoicePacketsListener(
    secret: String,
    private val ignoredShadowId: Int,
    private val targetMixer: AudioMixer,
    private val voiceSourceValidator: VoiceSourceValidator
) : SimpleChannelInboundHandler<DatagramPacket>() {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val decoder = AesBytesDecoder(secret)

    override fun channelRead0(ctx: ChannelHandlerContext?, pkt: DatagramPacket?) {
        val context = ctx ?: return
        val datagram = pkt ?: return
        val packet = try {
            RoutingContract.RtcPacket.parseFrom(datagram.content().nioBuffer())
        } catch (ex: InvalidProtocolBufferException) {
            logger.warn("Received bad RtcPacket.")
            return
        }
        // validation
        val userId = validateSource(packet) ?: return
        if (!packet.hasWave()) {
            return
        }
        if (packet.shadowId == ignoredShadowId) {
            return
        }
        // producing sound
        val data = packet.wave.payload.toByteArray()
        val payload = decoder.process(data)

        targetMixer.addPacket(
            userId,
            OrderedPacket(
                order = packet.wave.order,
                data = payload
            )
        )
    }

    private fun validateSource(packet: RoutingContract.RtcPacket): Long? =
        voiceSourceValidator.validateSource(packet.channelId, packet.shadowId)
}