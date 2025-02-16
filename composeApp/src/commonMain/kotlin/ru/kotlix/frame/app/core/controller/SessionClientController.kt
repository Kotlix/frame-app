package ru.kotlix.frame.app.core.controller

import io.netty.channel.Channel
import ru.kotlix.frame.session.api.SessionClientApi
import ru.kotlix.frame.session.api.dto.MessageNotifyData

class SessionClientController : SessionClientApi<Channel> {

    override fun messageNotify(context: Channel, notifyData: MessageNotifyData) {
        TODO("TBD")
    }
}