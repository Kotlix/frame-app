package presentation.viewstate

import base.presentation.ViewAction

sealed class AuthAction : ViewAction {
    data class OnLogin(val login: String, val password: String) : AuthAction()
    object OnRegister : AuthAction()
}