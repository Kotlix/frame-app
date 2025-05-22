package voice.audio

import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Line
import javax.sound.sampled.Mixer
import javax.sound.sampled.SourceDataLine
import javax.sound.sampled.TargetDataLine

class AudioServiceImpl : AudioService {

    private var inputMixer: Mixer.Info? = null
    private var inputLine: Line.Info? = null
    override var isInputMuted: Boolean = false
    private var targetDataLine: TargetDataLine? = null

    private var outputMixer: Mixer.Info? = null
    private var outputLine: Line.Info? = null
    override var isOutputMuted: Boolean = false
    private var sourceDataLine: SourceDataLine? = null

    override fun setInput(line: Line.Info, mixer: Mixer.Info?) {
        val target = if (mixer == null) {
            if (!AudioSystem.isLineSupported(line)) {
                throw IllegalArgumentException()
            }
            AudioSystem.getLine(line)
        } else {
            val mx = AudioSystem.getMixer(mixer)
            if (!mx.isLineSupported(line)) {
                throw IllegalArgumentException()
            }
            mx.getLine(line)
        } as TargetDataLine

        targetDataLine = target
        inputLine = line
        inputMixer = mixer
    }

    override fun setOutput(line: Line.Info, mixer: Mixer.Info?) {
        val source = if (mixer == null) {
            if (!AudioSystem.isLineSupported(line)) {
                throw IllegalArgumentException()
            }
            AudioSystem.getLine(line)
        } else {
            val mx = AudioSystem.getMixer(mixer)
            if (!mx.isLineSupported(line)) {
                throw IllegalArgumentException()
            }
            mx.getLine(line)
        } as SourceDataLine

        sourceDataLine = source
        outputLine = line
        outputMixer = mixer
    }

    override fun getInput(): TargetDataLine? = targetDataLine

    override fun getOutput(): SourceDataLine? = sourceDataLine

    override fun getInputMixer(): Mixer.Info? = inputMixer

    override fun getOutputMixer(): Mixer.Info? = outputMixer
}
