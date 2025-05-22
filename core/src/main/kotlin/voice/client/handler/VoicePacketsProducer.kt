package voice.client.handler

import com.google.protobuf.InvalidProtocolBufferException
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.socket.DatagramPacket
import org.slf4j.LoggerFactory
import ru.kotlix.frame.router.api.proto.RoutingContract
import voice.VoiceManager
import voice.audio.security.AesBytesDecoder
import java.time.Instant
import java.util.concurrent.ConcurrentMap
import kotlin.math.log

class VoicePacketsProducer(
    secret: String,
    private val lastPacket: ConcurrentMap<Int, Instant>,
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
        if (!validateSource(packet)) {
            return
        }
        if (VoiceManager.audioService.isOutputMuted) {
            return
        }
        val sdl = VoiceManager.audioService.getOutput() ?: return
        if (!sdl.isOpen) {
            return
        }
        if (!packet.hasWave()) {
            return
        }
        // producing sound
        val data = packet.wave.payload.toByteArray()
        val payload = decoder.process(data)

        sdl.write(payload, 0, payload.size)
        lastPacket[packet.shadowId] = Instant.now()
    }

    private fun validateSource(packet: RoutingContract.RtcPacket): Boolean =
        voiceSourceValidator.validateSource(packet.channelId, packet.shadowId)
}