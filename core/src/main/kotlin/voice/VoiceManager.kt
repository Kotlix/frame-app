package voice

import voice.audio.AudioService
import voice.audio.AudioServiceImpl
import voice.client.VoiceClient
import voice.client.VoiceClientImpl
import voice.dto.ConnectionGuide

object VoiceManager {
    var voiceClient: VoiceClient = VoiceClientImpl()
    var audioService: AudioService = AudioServiceImpl()
    var connectionGuide: ConnectionGuide? = null
}
