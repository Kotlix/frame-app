package voice.client

import com.google.protobuf.MessageLite
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.socket.DatagramPacket
import netty.NettyUdpClient
import org.slf4j.LoggerFactory
import ru.kotlix.frame.router.api.proto.RoutingContract
import ru.kotlix.frame.session.api.proto.SessionContract
import session.SessionManager
import session.client.ping
import session.client.wavePacket
import voice.VoiceManager
import voice.audio.AudioSystemTools
import voice.audio.security.AesBytesEncoder
import voice.audio.security.ReusableByteProcessor
import voice.client.handler.VoicePacketsProducer
import voice.dto.ConnectionGuide
import voice.dto.PartyUser
import java.net.InetSocketAddress
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread
import kotlin.math.log

class VoiceClientImpl : VoiceClient {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private val bufferSize = 32
    private val buffer = ByteArray(bufferSize)

    private lateinit var connectionGuide: ConnectionGuide
    private lateinit var udpClient: NettyUdpClient
    private lateinit var bytesEncoder: ReusableByteProcessor
    private var servingThread: Thread? = null
    private lateinit var voicePacketsProducer: VoicePacketsProducer
    private var partyUsers = listOf<PartyUser>()
    private var lastPackets = ConcurrentHashMap<Int, Instant>()

    override var pingDelayMs: Long = 5_000

    var uniqueOrder: Int = 0
        get() = field++

    private var isStarted: Boolean = false

    private lateinit var recipient: InetSocketAddress

    override suspend fun start(
        host: String,
        port: Int,
        secret: String
    ) {
        connectionGuide = VoiceManager.connectionGuide!!
        recipient = InetSocketAddress(host, port)

        voicePacketsProducer = VoicePacketsProducer(secret, lastPackets) { c, s ->
            if (connectionGuide.channelId != c) {
                return@VoicePacketsProducer false
            }
            return@VoicePacketsProducer partyUsers.find { it.shadowId == s }?.let { true } ?: false
        }
        bytesEncoder = AesBytesEncoder(secret)
        udpClient = NettyUdpClient(voicePacketsProducer)
        udpClient.connect()

        isStarted = true
        if (earlyPacket != null) {
            onVoiceNotify(earlyPacket!!)
        }
    }

    override fun isStarted(): Boolean = isStarted

    private fun serve(): Thread =
        thread {
            var pingAt = Instant.now().plusMillis(pingDelayMs)
            while (isStarted) {
                // ping part
                val now = Instant.now()
                if (now.isAfter(pingAt)) {
                    sendPacket(
                        ping(
                            channelId = connectionGuide.channelId,
                            shadowId = connectionGuide.shadowId
                        )
                    )
                    pingAt = now.plusMillis(pingDelayMs)
                }
                // voice part
                speak()
            }
        }

    private fun speak() {
        if (VoiceManager.audioService.isInputMuted) {
            return
        }
        val tdl = VoiceManager.audioService.getInput() ?: return
        if (!tdl.isOpen) {
            return
        }
        val read = tdl.read(buffer, 0, buffer.size)
        if (read != 0) {
            val waveEncrypted = bytesEncoder.process(buffer)
            sendPacket(
                wavePacket(
                    channelId = connectionGuide.channelId,
                    shadowId = connectionGuide.shadowId,
                    order = uniqueOrder,
                    waveData = waveEncrypted
                )
            )
        }
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

    override fun shutdown() {
        if (isStarted) {
            udpClient.disconnect()
            if (servingThread != null) {
                VoiceManager.audioService.getInput()?.let {
                    AudioSystemTools.close(it)
                }
                VoiceManager.audioService.getOutput()?.let {
                    AudioSystemTools.close(it)
                }
                servingThread!!.interrupt()
                servingThread = null
            }
            isStarted = false
        }
    }

    private var earlyPacket: SessionContract.VoiceNotify? = null

    override fun onVoiceNotify(packet: SessionContract.VoiceNotify) {
        logger.info("VOICE NOTIFY")
        if (!isStarted) {
            earlyPacket = packet
            logger.info("EARLY VOICE NOTIFY")
            return
        }
        partyUsers = (
                packet.partyList.map { PartyUser(it.userId, it.shadowId) } +
                        PartyUser(packet.changed.userId, packet.changed.shadowId)
                ).filter { it.shadowId != connectionGuide.shadowId }
        logger.info("Updated current state to $partyUsers")
        if (servingThread == null) {
            begin()
        }
    }

    private fun begin() {
        servingThread = serve()
        VoiceManager.audioService.getInput()?.let {
            AudioSystemTools.open(it)
        }
        VoiceManager.audioService.getOutput()?.let {
            AudioSystemTools.open(it)
        }
    }
}