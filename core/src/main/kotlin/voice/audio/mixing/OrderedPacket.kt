package voice.audio.mixing

data class OrderedPacket(
    val order: Int,
    val data: ByteArray
) : Comparable<OrderedPacket> {

    override fun compareTo(other: OrderedPacket): Int =
        -order.compareTo(other.order)
}
