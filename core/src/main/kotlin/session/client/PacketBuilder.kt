package session.client

import ru.kotlix.frame.session.api.proto.SessionContract

fun authRequest(
    token: String,
    pid: Long
): SessionContract.ClientPacket =
    SessionContract.ClientPacket.newBuilder()
        .setAuthReq(
            SessionContract.ClientPacket.AuthenticateRequest.newBuilder()
                .setToken(token)
                .setPid(pid)
                .build()
        ).build()

fun heartbeat(): SessionContract.ClientPacket =
    SessionContract.ClientPacket.newBuilder()
        .setHeartbeat(
            SessionContract.ClientPacket.Heartbeat.newBuilder().build()
        ).build()

fun messageNotifyPrefs(
    communityId: List<Long>,
    pid: Long
): SessionContract.ClientPacket =
    SessionContract.ClientPacket.newBuilder()
        .setMessageNotifyPrefs(
            SessionContract.ClientPacket.MessageNotifyPreferences.newBuilder()
                .addAllCommunityId(communityId)
                .setPid(pid)
        ).build()
