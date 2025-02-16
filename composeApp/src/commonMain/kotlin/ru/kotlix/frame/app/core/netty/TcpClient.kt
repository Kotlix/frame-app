package ru.kotlix.frame.app.core.netty

import io.netty.bootstrap.Bootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import ru.kotlix.frame.app.core.controller.SessionClientController
import ru.kotlix.frame.app.core.handler.SessionServerHandler
import ru.kotlix.frame.session.api.dto.AuthData
import ru.kotlix.frame.session.client.SessionServerApiHandle

class TcpClient {

    private val sessionClientController = SessionClientController()

    private val sessionHandler = SessionServerHandler(sessionClientController)

    private val tcpChannelInitializer = TcpChannelInitializer(sessionHandler)

    private val handle = SessionServerApiHandle(sessionHandler, 1_000)

    private val workerGroup = NioEventLoopGroup()

    private val bootstrap =
        Bootstrap()
            .group(workerGroup)
            .channel(NioSocketChannel::class.java)
            .handler(tcpChannelInitializer)

    fun start() {
        try {
            val ch = bootstrap.connect("localhost", 8082).sync().channel()
            val res = handle.authenticateData(ch, AuthData("123"))
            println("OUT: " + res.toString())
            ch.closeFuture().sync()
        } finally {
            workerGroup.shutdownGracefully()
        }
    }
}