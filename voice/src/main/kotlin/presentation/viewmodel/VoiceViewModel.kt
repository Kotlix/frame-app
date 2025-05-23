package presentation.viewmodel

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import cache.TimedCache
import data.model.ConnectionGuideEntity
import data.usecases.GetProfileInfoUseCase
import data.usecases.JoinVoiceChatUseCase
import data.usecases.LeaveVoiceUseCase
import exception.ConnectionFailedException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import session.SessionManager
import session.client.handler.ServerPacketFilter
import session.client.handler.ServerPacketListenerWatcher
import voice.VoiceManager
import voice.dto.ConnectionGuide
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class VoiceViewModel(
    private val joinVoiceChatUseCase: JoinVoiceChatUseCase,
    private val leaveVoiceUseCase: LeaveVoiceUseCase,
    private val getProfileInfoUseCase: GetProfileInfoUseCase
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private val viewModelScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    var errorMessage = mutableStateOf<String?>(null)
        private set

    var attendants = mutableStateOf(listOf<Pair<Long, Long>>())

    fun getToken(): String {
        return SessionManager.token ?: ""
    }

    fun joinVoice(voiceId: Long, callback: () -> Unit) {
        if (isStarted()) {
            logger.error("Already joined voice!")
            return
        }

        joinVoiceChatUseCase.execute(
            token = getToken(),  //// INSERT!!!!!!!!!!!!
            voiceId = voiceId
        ) { data, error ->
            viewModelScope.launch(Dispatchers.Default) {
                if (data != null) {
                    val host = data.hostAddress.split(':')[0]
                    val port = data.hostAddress.split(':')[1].toInt()

                    VoiceManager.connectionGuide =
                        ConnectionGuide(
                            channelId = data.channelId,
                            shadowId = data.shadowId,
                            host = host,
                            port = port
                        )

                    try {
                        VoiceManager.voiceClient.start(host, port, data.secret)
                        errorMessage.value = null
                    } catch (ex: ConnectionFailedException) {
                        errorMessage.value = ex.message
                    }
                    errorMessage.value = null
                } else {
                    errorMessage.value = error
                }

                callback()
            }
        }
    }

    private fun isStarted() = VoiceManager.connectionGuide != null

    fun leaveVoice(voiceId: Long, callback: () -> Unit) {
        if (!isStarted()) {
            logger.error("Not joined voice!")
            return
        }

        leaveVoiceUseCase.execute(
            token = getToken(),  //// INSERT!!!!!!!!!!!!
            voiceId = voiceId
        ) { error ->
            viewModelScope.launch(Dispatchers.Default) {
                VoiceManager.voiceClient.shutdown()
                VoiceManager.connectionGuide = null
                errorMessage.value = error
                callback()
            }
        }
    }

    private val userNames = TimedCache<Long, String>(ttlMillis = 5 * 60_000)

    fun findUsername(userId: Long, callback: (username: String) -> Unit) {
        val username = userNames.get(userId)
        if (username != null) {
            callback(username)
            return
        }

        getProfileInfoUseCase.execute(
            token = getToken(),
            userId = userId,
        ) { data, error ->
            if (data != null) {
                userNames.set(userId, data)
                callback(data)
            } else if (error != null) {
                logger.error(error)
                errorMessage.value = error
            }
        }
    }
}