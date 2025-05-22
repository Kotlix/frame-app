package voice.client.handler

fun interface VoiceSourceValidator {
    fun validateSource(channelId: Long, shadowId: Int): Boolean
}