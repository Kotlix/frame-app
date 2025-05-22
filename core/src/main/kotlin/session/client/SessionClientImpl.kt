package session.client

import AppConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import netty.NettyTcpClient
import org.slf4j.LoggerFactory
import ru.kotlix.frame.session.api.proto.SessionContract
import session.SessionManager
import exception.ConnectionFailedException
import session.client.handler.ServerPacketFilter
import session.client.handler.ServerPacketListenerRegistry
import session.client.handler.ServerPacketListenerRegistryImpl
import session.client.handler.ServerPacketListenerWatcher
import voice.VoiceManager
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SessionClientImpl : SessionClient {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private lateinit var tcpClient: NettyTcpClient
    private lateinit var heartbeatJob: Job
    private val packetHandler = ServerPacketListenerRegistryImpl()

    private var newPid: Long = 0
        get() = field++

    private fun newNettyTcpClient() =
        NettyTcpClient(
            AppConfig.BASE_SESSION_HOST,
            AppConfig.BASE_SESSION_PORT,
            packetHandler
        )

    private var isConnected: Boolean = false

    override fun isConnected(): Boolean {
        return isConnected
    }

    override suspend fun connectAuthorize(token: String) {
        tcpClient = newNettyTcpClient()

        logger.info("Trying to connect...")
        if (!tcpClient.connect()) {
            tcpClient.disconnect()
            logger.info("Connect failed.")
            throw ConnectionFailedException("Unable to connect.", ConnectionFailedException.Reason.NOT_CONNECTED)
        }
        logger.info("Connected!")
        logger.info("Trying to authorize...!")
        authorize(token)
        logger.info("Authorized.")
        heartbeatJob = startHeartbeat()
        logger.info("Heartbeat set.")
        isConnected = true
        syncVoiceClient()
    }

    private suspend fun authorize(
        token: String
    ) = suspendCoroutine { cont ->
        // register hook
        packetHandler.register(
            ServerPacketFilter.All,
            ServerPacketListenerWatcher.Once
        ) { pkt ->
            if (pkt != null) {
                when {
                    pkt.hasServerResponse() -> {
                        val goal = SessionContract.ServerPacket.ServerResponse.PacketStatus.ACK
                        val actual = pkt.serverResponse.packetStatus
                        if (actual != goal) {
                            cont.resumeWithException(
                                ConnectionFailedException(
                                    "Authentication failed with unexpected response $actual.",
                                    ConnectionFailedException.Reason.CONNECTED_UNEXPECTED
                                )
                            )
                        }
                        cont.resume(Unit)
                    }

                    pkt.hasSessionBreak() -> cont.resumeWithException(
                        ConnectionFailedException(
                            "Authentication failed with broken connection.",
                            causeToExceptionReason(pkt.sessionBreak.reason)
                        )
                    )

                    else -> {
                        cont.resumeWithException(
                            ConnectionFailedException(
                                "Authentication failed with unrecognised packet.",
                                ConnectionFailedException.Reason.CONNECTED_UNEXPECTED
                            )
                        )
                    }
                }
            } else {
                cont.resumeWithException(
                    ConnectionFailedException(
                        "Authentication failed with unrecognised packet.",
                        ConnectionFailedException.Reason.CONNECTED_UNEXPECTED
                    )
                )
            }
        }
        // init auth
        tcpClient.channel.writeAndFlush(authRequest(token, newPid))
    }

    private fun startHeartbeat(): Job =
        CoroutineScope(Dispatchers.Default)
            .launch {
                while (this.isActive) {
                    delay(60000)
                    tcpClient.channel.writeAndFlush(heartbeat())
                }
            }

    private fun syncVoiceClient() {
        packetHandler.register(
            { it.hasVoiceNotify() },
            ServerPacketListenerWatcher.Forever
        ) { packet ->
            VoiceManager.voiceClient.onVoiceNotify(packet!!.voiceNotify)
        }
    }

    private fun causeToExceptionReason(
        cause: SessionContract.ServerPacket.SessionBreak.BreakCause
    ): ConnectionFailedException.Reason = when (cause) {
        SessionContract.ServerPacket.SessionBreak.BreakCause.TIMED_OUT ->
            ConnectionFailedException.Reason.CONNECTED_TIMED_OUT

        SessionContract.ServerPacket.SessionBreak.BreakCause.WRONG_AUTH ->
            ConnectionFailedException.Reason.CONNECTED_WRONG_AUTH

        SessionContract.ServerPacket.SessionBreak.BreakCause.ALREADY_LOGGED ->
            ConnectionFailedException.Reason.CONNECTED_ALREADY_LOGGED

        SessionContract.ServerPacket.SessionBreak.BreakCause.ERROR ->
            ConnectionFailedException.Reason.CONNECTED_SERVER_ERROR

        SessionContract.ServerPacket.SessionBreak.BreakCause.UNRECOGNIZED ->
            ConnectionFailedException.Reason.CONNECTED_UNEXPECTED
    }

    override fun disconnect() {
        if (isConnected) {
            tcpClient.disconnect()
            heartbeatJob.cancel()
        }
    }

    override fun getPacketListener(): ServerPacketListenerRegistry {
        return packetHandler
    }

    private lateinit var lastPreferences: List<Long>
    override suspend fun updateMessageNotificationPreferences(communityId: List<Long>): Boolean {
        if (::lastPreferences.isInitialized && lastPreferences.containsAll(communityId)) {
            return true
        }
        val pid = newPid
        val result = suspendCoroutine { cont ->
            // first register
            packetHandler.register(
                ServerPacketFilter.ServerResponseForPid(pid),
                ServerPacketListenerWatcher.Once
            ) { pkt ->
                if (pkt != null) {
                    when (pkt.serverResponse.packetStatus) {
                        SessionContract.ServerPacket.ServerResponse.PacketStatus.ACK -> cont.resume(true)
                        SessionContract.ServerPacket.ServerResponse.PacketStatus.NACK -> cont.resume(false)
                        else -> cont.resumeWithException(
                            ConnectionFailedException(
                                "updateMessageNotificationPreferences failed with unknown response.",
                                ConnectionFailedException.Reason.CONNECTED_UNEXPECTED
                            )
                        )
                    }
                } else {
                    cont.resumeWithException(
                        ConnectionFailedException(
                            "updateMessageNotificationPreferences failed with server error.",
                            ConnectionFailedException.Reason.CONNECTED_UNEXPECTED
                        )
                    )
                }
            }
            // then write
            tcpClient.channel.writeAndFlush(messageNotifyPrefs(communityId, pid))
        }
        if (result) {
            lastPreferences = communityId
        }
        return result
    }
}
