package voice.audio

import org.slf4j.LoggerFactory
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Mixer
import javax.sound.sampled.SourceDataLine
import javax.sound.sampled.TargetDataLine

object AudioSystemTools {
    private val logger = LoggerFactory.getLogger(this::class.java)

    val audioFormat = AudioFormat(
        AudioFormat.Encoding.PCM_SIGNED,
        16000.0F,
        16,
        1,
        2,
        16000.0F,
        false
    )

    val audioFrameMs = 20
    val audioFrameBufferSize = audioFormat.sampleRate.toInt() * audioFrameMs / 1000 * audioFormat.frameSize

    fun getMixers() = AudioSystem.getMixerInfo().map { AudioSystem.getMixer(it) }

    fun getOutputs(mx: Mixer) = mx.sourceLineInfo
        .filter { SourceDataLine::class.java.isAssignableFrom(it.lineClass) }
        .map { mx.getLine(it) as SourceDataLine }

    fun getInputs(mx: Mixer) = mx.targetLineInfo
        .filter { TargetDataLine::class.java.isAssignableFrom(it.lineClass) }
        .map { mx.getLine(it) as TargetDataLine }

    fun open(line: TargetDataLine) {
        logger.info("Opened ${line.lineInfo} with format ${audioFormat}")
        line.open(audioFormat)
        line.start()
    }

    fun open(line: SourceDataLine) {
        logger.info("Opened ${line.lineInfo} with format ${audioFormat}")
        println(line.format)
        line.open(audioFormat)
        line.start()
    }

    fun close(line: TargetDataLine) {
        logger.info("Closed ${line.lineInfo}")
        line.close()
    }

    fun close(line: SourceDataLine) {
        logger.info("Closed ${line.lineInfo}")
        line.close()
    }
}
