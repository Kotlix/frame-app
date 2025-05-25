package voice.audio

import javax.sound.sampled.Line
import javax.sound.sampled.Mixer
import javax.sound.sampled.SourceDataLine
import javax.sound.sampled.TargetDataLine

interface AudioService {
    fun setInput(line: Line.Info, mixer: Mixer.Info? = null)

    fun setOutput(line: Line.Info, mixer: Mixer.Info? = null)

    var isInputMuted: Boolean

    var isOutputMuted: Boolean

    fun getInput(): TargetDataLine?

    fun getInputMixer(): Mixer.Info?

    fun getOutput(): SourceDataLine?

    fun getOutputMixer(): Mixer.Info?
}
