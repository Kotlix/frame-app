package presentation.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.rememberWindowState
import presentation.viewmodel.AuthViewModel

class CodeVerifyPopup {
    @Composable
    fun CodeVerifyPopup(viewModel: AuthViewModel, onClose: () -> Unit) {
        var code by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        var isLoading by remember { mutableStateOf(false) }

        val verifyError by viewModel.verifyError
        val isVerified = viewModel.isVerified

        Window(
            onCloseRequest = onClose,
            title = "Verify Code",
            state = rememberWindowState().apply {
                placement = WindowPlacement.Floating
//                width(400.dp)
//                height = 300.dp
            }
        ) {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column(
                        modifier = Modifier
                            .padding(32.dp)
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        TextField(
                            value = code,
                            onValueChange = { code = it },
                            label = { Text("Enter verification code") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (code.isBlank()) {
                                    errorMessage = "Code cannot be empty"
                                } else {
                                    errorMessage = null
                                    isLoading = true
                                    viewModel.verifyCode(code)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text("Join")
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        when {
//                            errorMessage != null -> Text(
//                                errorMessage!!,
//                                color = MaterialTheme.colors.error
//                            )
                            isVerified.value -> Text(
                                "Successfully joined",
                                color = MaterialTheme.colors.primary
                            )
                            verifyError != null -> Text(
                                verifyError!!,
                                color = MaterialTheme.colors.error
                            )
                            //isLoading -> CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}
