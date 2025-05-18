package session

import session.client.SessionClientImpl

object SessionManager {
    var token: String? = null
    var rawToken: String? = null
    var userId: Long? = null
    var sessionClient = SessionClientImpl()
}
