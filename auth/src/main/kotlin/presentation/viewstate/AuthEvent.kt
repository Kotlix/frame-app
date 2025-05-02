package presentation.viewstate

import base.presentation.ViewEvent

sealed class AuthEvent : ViewEvent {
    data class OnLoginComplete(val token: String): AuthEvent()
    class UnknownError(error: String) : AuthEvent()
    object OnRegisterComplete: AuthEvent()
}