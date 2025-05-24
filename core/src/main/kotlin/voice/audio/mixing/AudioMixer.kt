package voice.audio.mixing

interface AudioMixer {
    fun addPacket(userId: Long, packet: OrderedPacket)
    fun mixingEntrypoint()
}