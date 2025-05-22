package voice.audio

import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Mixer
import javax.sound.sampled.SourceDataLine
import javax.sound.sampled.TargetDataLine

object AudioSystemTools {

    fun getMixers() = AudioSystem.getMixerInfo().map { AudioSystem.getMixer(it) }

    fun getOutputs(mx: Mixer) = mx.sourceLineInfo
        .filter { SourceDataLine::class.java.isAssignableFrom(it.lineClass) }
        .map { mx.getLine(it) as SourceDataLine }

    fun getInputs(mx: Mixer) = mx.targetLineInfo
        .filter { TargetDataLine::class.java.isAssignableFrom(it.lineClass) }
        .map { mx.getLine(it) as TargetDataLine }

    fun open(line: TargetDataLine) {
        line.open(line.format)
        line.start()
    }

    fun open(line: SourceDataLine) {
        line.open(line.format)
        line.start()
    }

    fun close(line: TargetDataLine) {
        line.close()
    }

    fun close(line: SourceDataLine) {
        line.close()
    }
}
