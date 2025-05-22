package voice.audio.security

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class AesBytesDecoder(secret: String) : ReusableByteProcessor {
    private val ALGO = "AES"
    private val cipher = Cipher.getInstance(ALGO).apply {
        init(
            Cipher.DECRYPT_MODE,
            SecretKeySpec(
                crop32(secret.toByteArray(Charsets.UTF_8)),
                ALGO
            )
        )
    }

    private fun crop32(byteArray: ByteArray): ByteArray =
        ByteArray(32) {
            try {
                byteArray[it]
            } catch (ex: IndexOutOfBoundsException) {
                0
            }
        }

    override fun process(input: ByteArray): ByteArray = cipher.doFinal(input)
}