package voice.audio.mixing

interface AudioMixer {
    fun addPacket(shadowId: Int, packet: OrderedPacket)
    fun mixingEntrypoint()
}