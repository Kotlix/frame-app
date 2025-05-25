package presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import data.model.response.ProfileInfo
import data.usecase.profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import session.SessionManager

class ProfileViewModel(
    private val changeProfilePasswordUseCase: ChangeProfilePasswordUseCase,
    private val changeProfilePasswordApplyUseCase: ChangeProfilePasswordApplyUseCase,
    private val changeProfileUserNameUseCase: ChangeProfileUserNameUseCase,
    private val changeProfileUsernameApplyUseCase: ChangeProfileUsernameApplyUseCase,
    private val changeProfileEmailUseCase: ChangeProfileEmailUseCase,
    private val changeProfileEmailApplyUseCase: ChangeProfileEmailApplyUseCase,
    private val getMyProfileInfo: GetMyProfileInfo
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private val viewModelScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    var errorMessage = mutableStateOf<String?>(null)
        private set

    var isVerified = mutableStateOf<Boolean>(false)
        private set

    var profile = mutableStateOf<ProfileInfo?>(null)
        private set




    fun getToken(): String {
        return SessionManager.token ?: ""
    }

    fun changeUserName(newUserName: String, callback: () -> Unit) {
        changeProfileUserNameUseCase.execute(
            token = getToken(),  //// INSERT!!!!!!!!!!!!
            newUserName = newUserName
        ) { error ->
            viewModelScope.launch(Dispatchers.Default) {
                if (error == null) {
                    errorMessage.value = null
                } else {
                    errorMessage.value = error
                }

                callback()
            }
        }
    }

    fun changeUserNameApply(secret: String, callback: () -> Unit) {
        changeProfileUsernameApplyUseCase.execute(
            token = getToken(),  //// INSERT!!!!!!!!!!!!
            secret = secret
        ) { error ->
            viewModelScope.launch(Dispatchers.Default) {
                if (error == null) {
                    isVerified.value = true
                    errorMessage.value = null
                } else {
                    errorMessage.value = error
                }

                callback()
            }
        }
    }

    fun changeEmail(newEmail: String, callback: () -> Unit) {
        changeProfileEmailUseCase.execute(
            token = getToken(),  //// INSERT!!!!!!!!!!!!
            newEmail = newEmail
        ) { error ->
            viewModelScope.launch(Dispatchers.Default) {
                if (error == null) {
                    errorMessage.value = null
                } else {
                    errorMessage.value = error
                }

                callback()
            }
        }
    }

    fun changeEmailApply(secret: String, callback: () -> Unit) {
        changeProfileEmailApplyUseCase.execute(
            token = getToken(),  //// INSERT!!!!!!!!!!!!
            secret = secret
        ) { error ->
            viewModelScope.launch(Dispatchers.Default) {
                if (error == null) {
                    isVerified.value = true
                    errorMessage.value = null
                } else {
                    errorMessage.value = error
                }

                callback()
            }
        }
    }

    fun changePassword(newPassword: String, callback: () -> Unit) {
        changeProfilePasswordUseCase.execute(
            token = getToken(),  //// INSERT!!!!!!!!!!!!
            newPassword = newPassword
        ) { error ->
            viewModelScope.launch(Dispatchers.Default) {
                if (error == null) {
                    errorMessage.value = null
                } else {
                    errorMessage.value = error
                }

                callback()
            }
        }
    }

    fun changePasswordApply(secret: String, callback: () -> Unit) {
        changeProfilePasswordApplyUseCase.execute(
            token = getToken(),  //// INSERT!!!!!!!!!!!!
            secret = secret
        ) { error ->
            viewModelScope.launch(Dispatchers.Default) {
                if (error == null) {
                    isVerified.value = true
                    errorMessage.value = null
                } else {
                    errorMessage.value = error
                }

                callback()
            }
        }
    }

    fun getMyProfileInfo(callback: () -> Unit) {
        getMyProfileInfo.execute(
            token = getToken(),  //// INSERT!!!!!!!!!!!!
        ) { data, error ->
            viewModelScope.launch(Dispatchers.Default) {
                if (data != null) {
                    profile.value = data
                    errorMessage.value = null
                } else {
                    errorMessage.value = error
                }

                callback()
            }
        }
    }

}