package voice.audio.security

interface ReusableByteProcessor {
    fun process(input: ByteArray): ByteArray
}