package voice.client

import ru.kotlix.frame.session.api.proto.SessionContract.VoiceNotify

interface VoiceClient {
    var pingDelayMs: Long

    suspend fun start(host: String, port: Int, secret: String)

    fun isStarted(): Boolean

    fun shutdown()

    fun onVoiceNotify(packet: VoiceNotify)

    fun bindAttendantsCallback(
        attendantsCallback: (List<Long>) -> Unit,
        attendantsSpeakingCallback: (Long, Boolean) -> Unit
    )
}