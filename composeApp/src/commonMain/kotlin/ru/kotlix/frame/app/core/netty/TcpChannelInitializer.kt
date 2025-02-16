package ru.kotlix.frame.app.core.netty

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import ru.kotlix.frame.session.client.handler.SessionHandlerOnClient

class TcpChannelInitializer(
    private val sessionHandlerOnClient: SessionHandlerOnClient
) : ChannelInitializer<SocketChannel>() {

    override fun initChannel(ch: SocketChannel?) {
        val pipeline = ch?.pipeline()
            ?: throw RuntimeException("Channel not initialized")

        pipeline.addLast(LoggingHandler(LogLevel.INFO))
        pipeline.addLast(sessionHandlerOnClient)
    }
}