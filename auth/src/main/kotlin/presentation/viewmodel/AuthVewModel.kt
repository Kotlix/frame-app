package presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import data.usecase.LoginUseCase
import data.usecase.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import presentation.viewstate.AuthState

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) {

    var loginState = mutableStateOf("")
        private set

    var passwordState = mutableStateOf("")
        private set

    var authState = mutableStateOf<AuthState>(AuthState.Idle)
        private set

    private val _registrationError = MutableStateFlow<String?>(null)
    val registrationError: StateFlow<String?> get() = _registrationError

    private val _isRegistered = MutableStateFlow(false)
    val isRegistered: StateFlow<Boolean> get() = _isRegistered

    fun updateLoginState(newLogin: String) {
        loginState.value = newLogin
    }

    fun updatePasswordState(newPassword: String) {
        passwordState.value = newPassword
    }

    fun login() {
        val login = loginState.value
        val password = passwordState.value

        loginUseCase.execute(login, password) { token, error ->
            if (token != null) {
                authState.value = AuthState.LoginComplete
            } else {
                authState.value = AuthState.Error(error ?: "Unknown error")
            }
        }
    }

    fun register(nickname: String, login: String, email: String, password: String) {
        registerUseCase.execute(
            login = login,
            password = password,
            username = nickname,
            email = email
        ) { error ->
            if (error == null) {
                _isRegistered.value = true
            } else {
                _registrationError.value = error
            }
        }
    }
}

