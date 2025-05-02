package data.model

data class BasicRegisterRequest(
    val login: String,
    val password: String,
    val username: String,
    val email: String
)