package voice.audio.producer

import com.google.protobuf.MessageLite
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.socket.DatagramPacket
import netty.NettyUdpClient
import ru.kotlix.frame.router.api.proto.RoutingContract
import session.client.ping
import session.client.wavePacket
import voice.VoiceManager
import voice.audio.AudioSystemTools
import voice.audio.security.AesBytesEncoder
import voice.dto.ConnectionGuide
import java.net.InetSocketAddress
import javax.sound.sampled.TargetDataLine

class AudioProducerImpl(
    secret: String,
    private val udpClient: NettyUdpClient,
    private val recipient: InetSocketAddress,
    private val connectionGuide: ConnectionGuide
) : AudioProducer {
    private val encoder = AesBytesEncoder(secret)
    private val buffer = ByteArray(AudioSystemTools.audioFrameBufferSize)

    var uniqueOrder: Int = 0
        get() = field++

    override fun produceEntrypoint() {
        val tdl = VoiceManager.audioService.getInput() ?: return
        if (!tdl.isOpen) {
            return
        }

        fillBuffer(tdl)
        if (VoiceManager.audioService.isInputMuted) {
            return
        }
        val waveEncrypted = encoder.process(buffer)
        sendPacket(
            wavePacket(
                channelId = connectionGuide.channelId,
                shadowId = connectionGuide.shadowId,
                order = uniqueOrder,
                waveData = waveEncrypted
            )
        )
    }

    private fun fillBuffer(tdl: TargetDataLine) {
        val total = buffer.size
        var offset = 0
        while (offset < total) {
            val bytesRead = tdl.read(buffer, offset, total - offset)
            if (bytesRead <= 0) break
            offset += bytesRead
        }
    }

    override fun producePing() {
        sendPacket(
            ping(
                channelId = connectionGuide.channelId,
                shadowId = connectionGuide.shadowId
            )
        )
    }

    private fun sendPacket(packet: RoutingContract.RtcPacket) {
        udpClient.channel.writeAndFlush(DatagramPacket(encodeProto(packet), recipient))
    }

    private fun encodeProto(packet: Any): ByteBuf? {
        if (packet is MessageLite) {
            return Unpooled.wrappedBuffer(packet.toByteArray())
        } else if (packet is MessageLite.Builder) {
            return Unpooled.wrappedBuffer(packet.build().toByteArray())
        }
        return null
    }
}