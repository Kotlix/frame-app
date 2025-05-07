package presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import data.usecase.LoginUseCase
import data.usecase.RegisterUseCase
import data.usecase.VerifyCodeUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import presentation.viewstate.AuthState
import session.SessionManager

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val verifyCodeUseCase: VerifyCodeUseCase
) {

    private val viewModelScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

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

    var verifyError = mutableStateOf<String?>(null)
        private set

    var isVerified = mutableStateOf<Boolean>(false)
        private set


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
            viewModelScope.launch(Dispatchers.Default) {
                if (token != null) {
                    SessionManager.token = "Bearer " + token
                    authState.value = AuthState.LoginComplete
                    println(token.toString())

                } else {
                    println(error.toString())
                    authState.value = AuthState.Error(error ?: "Unknown error")
                }
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
            viewModelScope.launch(Dispatchers.Default) {
                if (error == null) {
                    _isRegistered.value = true
                } else {
                    _registrationError.value = error
                }
            }
        }
    }

    fun verifyCode(code: String) {
        verifyCodeUseCase.execute(
            code = code
        ) { error ->
            viewModelScope.launch(Dispatchers.Default) {
                if (error == null) {
                    isVerified.value = true
                    verifyError.value = null
                } else {
                    isVerified.value = false
                    verifyError.value = error
                }
            }
        }
    }
}

