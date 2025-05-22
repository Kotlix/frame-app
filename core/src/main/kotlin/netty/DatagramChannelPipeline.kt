package netty

import io.netty.channel.ChannelInitializer
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.socket.DatagramChannel
import io.netty.channel.socket.DatagramPacket
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.protobuf.ProtobufDecoder
import io.netty.handler.codec.protobuf.ProtobufEncoder
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import ru.kotlix.frame.router.api.proto.RoutingContract
import ru.kotlix.frame.session.api.proto.SessionContract

class DatagramChannelPipeline(
    private val voicePacketsHandler: SimpleChannelInboundHandler<DatagramPacket>,
) : ChannelInitializer<DatagramChannel>() {
    override fun initChannel(ch: DatagramChannel?) {
        ch?.pipeline()?.apply {
//            addLast(LoggingHandler(LogLevel.DEBUG))

            addLast(voicePacketsHandler)
        }
    }
}