package presentation.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.graphics.Color
import org.koin.core.context.startKoin
import presentation.viewmodel.AuthViewModel
import presentation.viewstate.AuthEvent
import presentation.viewstate.AuthState

class AuthView() {

    @Composable
    fun LoginScreen(viewModel: AuthViewModel, onRegisterClick: () -> Unit, onClose: () -> Unit) {
        val login by viewModel.loginState
        val password by viewModel.passwordState
        val authState by viewModel.authState

        var loginError by remember { mutableStateOf<String?>(null) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                Column(
                    modifier = Modifier
                        .padding(32.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Column(
                        modifier = Modifier.widthIn(max = 400.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("Login", style = MaterialTheme.typography.h6)

                        TextField(
                            value = login,
                            onValueChange = {
                                viewModel.updateLoginState(it)
                                loginError = null
                            },
                            label = { Text("Login") },
                            isError = loginError != null,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                            modifier = Modifier.fillMaxWidth()
                        )

                        TextField(
                            value = password,
                            onValueChange = {
                                viewModel.updatePasswordState(it)
                            },
                            label = { Text("Password") },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (loginError != null) {
                            Text(loginError!!, color = Color.Red, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                if (login.isEmpty()) {
                                    loginError = "Login must not be empty!"
                                } else {
                                    if(password.isEmpty()) {
                                        loginError = "Password must not be empty!"
                                    } else {
                                        viewModel.login()
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text("Log In")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(
                            onClick = onRegisterClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text("Register")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        when (authState) {
                            is AuthState.LoginComplete -> {
                                Text("Authorized!", color = Color.Green)
                                LaunchedEffect(authState) {
                                    onClose()
                                }
                            }
                            is AuthState.Error -> Text(
                                (authState as AuthState.Error).error,
                                color = Color.Red
                            )
                            else -> {}
                        }
                    }
                }
            }
        }
    }



    @Composable
    fun AuthApp(viewModel: AuthViewModel, callback: () -> Unit) {
        var showRegisterPopup by remember { mutableStateOf(false) }
        var showAuthWindow by remember { mutableStateOf(true) }

        val onRegisterClick: () -> Unit = {
            showRegisterPopup = true
        }

        val onCloseRegisterPopup: () -> Unit = {
            showRegisterPopup = false
        }

        if (showRegisterPopup) {
            RegisterPopup().RegisterPopup(viewModel = viewModel, onClose = onCloseRegisterPopup)
        } else {
            LoginScreen(viewModel = viewModel, onRegisterClick = onRegisterClick, onClose = callback)
        }
    }

    @Composable
    @Preview
    fun AuthAppPreview(viewModel: AuthViewModel, callback: () -> Unit) {
        AuthView().AuthApp(viewModel = viewModel, callback)
    }
}
