package presentation.viewstate

import base.presentation.ViewState

sealed class AuthState : ViewState{
    object Idle : AuthState()
    object LoginComplete : AuthState()
    data class Error(val error: String) : AuthState()
}