package presentation.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.window.application
import presentation.viewmodel.AuthViewModel

class RegisterPopup {

    @Composable
    fun RegisterPopup(viewModel: AuthViewModel, onClose: () -> Unit) {
        var nickname by remember { mutableStateOf("") }
        var login by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        var isLoading by remember { mutableStateOf(false) }

        val registrationError = viewModel.registrationError.collectAsState().value
        val isRegistered = viewModel.isRegistered.collectAsState().value

        if (isRegistered) {
            onClose()
        }

        Window(
            onCloseRequest = onClose,
            state = rememberWindowState().apply {
                placement = WindowPlacement.Maximized
            },
            title = "Registration"
        ) {
            MaterialTheme {
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
                        Text(
                            "Register",
                            style = MaterialTheme.typography.h4,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        Column(
                            modifier = Modifier.widthIn(max = 400.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            TextField(
                                value = nickname,
                                onValueChange = { nickname = it },
                                label = { Text("Nickname") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            TextField(
                                value = login,
                                onValueChange = { login = it },
                                label = { Text("Login") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            TextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            TextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Password") },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (errorMessage != null) {
                                Text(
                                    errorMessage!!,
                                    color = MaterialTheme.colors.error,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }

                            if (registrationError != null) {
                                Text(
                                    registrationError,
                                    color = MaterialTheme.colors.error,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }

                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
                            }

                            Button(
                                onClick = {
                                    if (nickname.isEmpty() || login.isEmpty() || email.isEmpty() || password.isEmpty()) {
                                        errorMessage = "All fields must be filled!"
                                    } else {
                                        errorMessage = null
                                        isLoading = true
                                        // Вызов метода регистрации в ViewModel
                                        viewModel.register(nickname, login, email, password)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Text("Register")
                            }

                            TextButton(
                                onClick = {},
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Text("Already have an account?")
                            }

                            TextButton(
                                onClick = onClose,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Log In")
                            }
                        }
                    }
                }
            }
        }
    }
}
