package exception

class ConnectionFailedException(message: String, val reason: Reason) : RuntimeException(message) {
    enum class Reason {
        NOT_CONNECTED,
        CONNECTED_TIMED_OUT,
        CONNECTED_WRONG_AUTH,
        CONNECTED_ALREADY_LOGGED,
        CONNECTED_SERVER_ERROR,
        CONNECTED_UNEXPECTED
    }
}