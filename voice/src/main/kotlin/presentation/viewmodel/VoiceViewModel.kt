package presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import data.model.ConnectionGuideEntity
import data.usecases.JoinVoiceChatUseCase
import data.usecases.LeaveVoiceUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import session.SessionManager

class VoiceViewModel(
    private val joinVoiceChatUseCase: JoinVoiceChatUseCase,
    private val leaveVoiceUseCase: LeaveVoiceUseCase
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private val viewModelScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    var errorMessage = mutableStateOf<String?>(null)
        private set

    var connectionGuide = mutableStateOf<ConnectionGuideEntity?>(null)
        private set

    fun getToken(): String {
        return SessionManager.token ?: ""
    }

    fun joinVoice(voiceId: Long, callback: () -> Unit) {
        joinVoiceChatUseCase.execute(
            token = getToken(),  //// INSERT!!!!!!!!!!!!
            voiceId = voiceId
        ) { data, error ->
            viewModelScope.launch(Dispatchers.Default) {
                if (data != null) {
                    connectionGuide.value = data
                    errorMessage.value = null
                } else {
                    errorMessage.value = error
                }

                callback()
            }
        }
    }

    fun leaveVoice(voiceId: Long, callback: () -> Unit) {
        leaveVoiceUseCase.execute(
            token = getToken(),  //// INSERT!!!!!!!!!!!!
            voiceId = voiceId
        ) {error ->
            viewModelScope.launch(Dispatchers.Default) {
                errorMessage.value = error
                callback()
            }
        }
    }
}