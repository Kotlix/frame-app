package ru.kotlix.frame.app.core.handler

import io.netty.channel.ChannelHandler.Sharable
import ru.kotlix.frame.app.core.controller.SessionClientController
import ru.kotlix.frame.session.client.handler.SessionHandlerOnClient

@Sharable
class SessionServerHandler(
    sessionClientController: SessionClientController
) : SessionHandlerOnClient(sessionClientController)