package voice.audio.mixing

import org.slf4j.LoggerFactory
import voice.VoiceManager
import voice.audio.AudioSystemTools
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.PriorityBlockingQueue

class AudioMixerImpl(
    private val onPacket: (Long, Boolean) -> Unit
) : AudioMixer {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val streamBuffers = ConcurrentHashMap<Long, PriorityBlockingQueue<OrderedPacket>>()

    private val lastPackets = ConcurrentHashMap<Long, Long>()
    private val bufferSize = AudioSystemTools.audioFrameBufferSize
    private val silenceBuffer = ByteArray(bufferSize)

    override fun addPacket(userId: Long, packet: OrderedPacket) {
        val queue = streamBuffers.computeIfAbsent(userId) {
            PriorityBlockingQueue<OrderedPacket>(11)
        }

        queue.offer(packet)
        val stamp = System.currentTimeMillis()
        lastPackets[userId] = stamp
    }

    override fun mixingEntrypoint() {
        mix()
        cleanInactiveUsers()
    }

    private fun mix() {
        if (VoiceManager.audioService.isOutputMuted) {
            return
        }
        val sdl = VoiceManager.audioService.getOutput() ?: return
        if (!sdl.isOpen) return

        val mixedFrame = ByteArray(bufferSize)

        streamBuffers.forEach { (id, queue) ->
            var data = queue.poll()?.data

            if (data != null) {
                onPacket(id, true)
            } else {
                data = silenceBuffer
                onPacket(id, false)
            }

            mixInto(mixedFrame, data)
            queue.clear()
        }

        sdl.write(mixedFrame, 0, mixedFrame.size)
    }

    private fun mixInto(output: ByteArray, input: ByteArray) {
        var i = 0
        while (i + 1 < input.size && i + 1 < output.size) {
            val sampleOut = ((output[i + 1].toInt() shl 8) or (output[i].toInt() and 0xFF)).toShort()
            val sampleIn = ((input[i + 1].toInt() shl 8) or (input[i].toInt() and 0xFF)).toShort()

            val mixed = (sampleOut + sampleIn).coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()

            output[i] = (mixed.toInt() and 0xFF).toByte()
            output[i + 1] = ((mixed.toInt() shr 8) and 0xFF).toByte()

            i += 2
        }
    }

    private fun cleanInactiveUsers() {
        val now = System.currentTimeMillis()
        streamBuffers.entries.removeIf { (shid, queue) ->
            val last = lastPackets[shid] ?: return@removeIf true
            now - last > 5000
        }
    }
}