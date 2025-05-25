package session.client

import com.google.protobuf.ByteString
import ru.kotlix.frame.router.api.proto.RoutingContract
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

fun ping(
    channelId: Long,
    shadowId: Int,
): RoutingContract.RtcPacket =
    RoutingContract.RtcPacket.newBuilder()
        .setPing(RoutingContract.Ping.newBuilder().build())
        .setChannelId(channelId)
        .setShadowId(shadowId)
        .build()

fun wavePacket(
    channelId: Long,
    shadowId: Int,
    order: Int,
    waveData: ByteArray
): RoutingContract.RtcPacket =
    RoutingContract.RtcPacket.newBuilder()
        .setWave(
            RoutingContract.WavePacket.newBuilder()
                .setOrder(order)
                .setPayload(ByteString.copyFrom(waveData))
        )
        .setChannelId(channelId)
        .setShadowId(shadowId)
        .build()
