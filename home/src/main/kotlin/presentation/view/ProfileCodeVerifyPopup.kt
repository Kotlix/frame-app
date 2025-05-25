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
import org.slf4j.LoggerFactory
import presentation.viewmodel.ProfileViewModel

class ProfileCodeVerifyPopup {
    private val logger = LoggerFactory.getLogger(ProfileCodeVerifyPopup::class.java)
    @Composable
    fun CodeVerifyPopup(viewModel: ProfileViewModel,
                        verifyMethod: (String) -> Unit,
                        onClose: () -> Unit) {
        var code by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        var isLoading by remember { mutableStateOf(false) }

        val verifyError by viewModel.errorMessage
        var isVerified by viewModel.isVerified


        LaunchedEffect("one_call") {
            isVerified = false
        }

        if (isVerified) {
            logger.info("CLOSED")
            onClose()
        }

        Window(
            onCloseRequest = onClose,
            title = "Verify Code",
            state = rememberWindowState().apply {
                placement = WindowPlacement.Floating
//                width(400.dp)
//                height = 300.dp
            },
            alwaysOnTop = true,
           // undecorated = true
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
                                    verifyMethod(code)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text("Confirm")
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                onClose()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text("Cancel")
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        when {
//                            errorMessage != null -> Text(
//                                errorMessage!!,
//                                color = MaterialTheme.colors.error
//                            )
                            isVerified -> {
                                Text(
                                    "Successfully joined",
                                    color = MaterialTheme.colors.primary
                                )
                                onClose()
                            }
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