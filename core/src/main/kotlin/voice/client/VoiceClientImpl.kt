package voice.client

import com.google.protobuf.MessageLite
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.socket.DatagramPacket
import netty.NettyUdpClient
import org.slf4j.LoggerFactory
import ru.kotlix.frame.router.api.proto.RoutingContract
import ru.kotlix.frame.session.api.proto.SessionContract
import session.client.ping
import session.client.wavePacket
import voice.VoiceManager
import voice.audio.AudioSystemTools
import voice.audio.mixing.AudioMixer
import voice.audio.mixing.AudioMixerImpl
import voice.audio.security.AesBytesEncoder
import voice.audio.security.ReusableByteProcessor
import voice.audio.handler.VoicePacketsListener
import voice.audio.producer.AudioProducer
import voice.audio.producer.AudioProducerImpl
import voice.dto.ConnectionGuide
import voice.dto.PartyUser
import java.net.InetSocketAddress
import kotlin.concurrent.thread

class VoiceClientImpl : VoiceClient {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private lateinit var connectionGuide: ConnectionGuide
    private lateinit var udpClient: NettyUdpClient
    private lateinit var bytesEncoder: ReusableByteProcessor
    private lateinit var audioMixer: AudioMixer
    private lateinit var audioProducer: AudioProducer
    private var partyUsers = listOf<PartyUser>()
    private var servingThread: Thread? = null
    private lateinit var recipient: InetSocketAddress

    private var isStarted: Boolean = false

    override suspend fun start(
        host: String,
        port: Int,
        secret: String
    ) {
        connectionGuide = VoiceManager.connectionGuide!!
        recipient = InetSocketAddress(host, port)
        bytesEncoder = AesBytesEncoder(secret)
        audioMixer = AudioMixerImpl { userId, speak ->
            if (::attendantsSpeakingCallback.isInitialized) {
                attendantsSpeakingCallback(userId, speak)
            }
        }

        udpClient = NettyUdpClient(
            VoicePacketsListener(secret, connectionGuide.shadowId, audioMixer) { c, s ->
                if (connectionGuide.channelId != c) {
                    return@VoicePacketsListener null
                }
                val userId = partyUsers.find { it.shadowId == s }?.userId
                return@VoicePacketsListener userId
            }
        )
        udpClient.connect()
        audioProducer = AudioProducerImpl(secret, udpClient, recipient, connectionGuide) { speaking ->
            if (::attendantsSpeakingCallback.isInitialized) {
                ownUserId?.let {
                    attendantsSpeakingCallback(it, speaking)
                }
            }
        }

        isStarted = true
        if (earlyPacket != null) {
            onVoiceNotify(earlyPacket!!)
        }
    }

    override fun isStarted(): Boolean = isStarted

    override var pingDelayMs: Long = 5_000

    private fun serve(): Thread =
        thread {
            var pingAt = System.currentTimeMillis() + pingDelayMs
            var playAt = System.currentTimeMillis() + AudioSystemTools.audioFrameMs
            while (isStarted) {
                val now = System.currentTimeMillis()

                if (now >= pingAt) {
                    audioProducer.producePing()
                    pingAt = now + pingDelayMs
                }
                if (now >= playAt) {
                    audioMixer.mixingEntrypoint()
                    audioProducer.produceEntrypoint()
                    playAt = now + AudioSystemTools.audioFrameMs
                }

                try {
                    Thread.sleep(1L)
                } catch (ex: InterruptedException) {
                    break
                }
            }
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

    override fun onVoiceNotify(packet: SessionContract.VoiceNotify) {
        logger.info("Voice notify received.")
        handleEarlyPacket(packet)
        if (!isStarted) {
            return
        }
        handleParty(packet)

        servingThread ?: begin()
    }

    private var earlyPacket: SessionContract.VoiceNotify? = null
    private fun handleEarlyPacket(packet: SessionContract.VoiceNotify) {
        if (!isStarted) {
            earlyPacket = packet
        }
    }

    private var ownUserId: Long? = null
    private fun handleParty(packet: SessionContract.VoiceNotify) {
        partyUsers = packet.partyList.map { PartyUser(it.userId, it.shadowId) }

        if (packet.action == SessionContract.VoiceNotify.Action.JOINED) {
            if (packet.changed.shadowId == connectionGuide.shadowId) {
                ownUserId = packet.changed.userId
            }
            partyUsers += PartyUser(packet.changed.userId, packet.changed.shadowId)
        }

        logger.info("Updated current state to $partyUsers")

        if (::attendantsCallback.isInitialized) {
            attendantsCallback(partyUsers.map { it.userId })
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

    private lateinit var attendantsCallback: (List<Long>) -> Unit
    private lateinit var attendantsSpeakingCallback: (Long, Boolean) -> Unit

    override fun bindAttendantsCallback(
        attendantsCallback: (List<Long>) -> Unit,
        attendantsSpeakingCallback: (Long, Boolean) -> Unit
    ) {
        logger.info("Registered UI callbacks")

        this.attendantsCallback = attendantsCallback
        this.attendantsSpeakingCallback = attendantsSpeakingCallback

        attendantsCallback(partyUsers.map { it.userId })
    }
}