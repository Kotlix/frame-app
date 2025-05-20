package presentation.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.rememberWindowState
import presentation.viewmodel.HomeViewModel

class CreateVoicePopup {
    @Composable
    fun CreateVoicePopup(viewModel: HomeViewModel, workingDirectory: Long, onClose: () -> Unit) {
        var name by remember { mutableStateOf("") }
        var communityId by viewModel.selectedCommunityId
        var errorMessage by remember { mutableStateOf<String?>(null) }
        var isLoading by remember { mutableStateOf(false) }

        var error by viewModel.errorMessage


        Window(
            onCloseRequest = onClose,
            state = rememberWindowState().apply {
                placement = WindowPlacement.Floating
            },
            title = "Create Voice"
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
                            "Create Voice",
                            style = MaterialTheme.typography.h4,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        Column(
                            modifier = Modifier.widthIn(max = 400.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            TextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Voice Name") },
                                modifier = Modifier.fillMaxWidth()
                            )

//                            TextField(
//                                value = description,
//                                onValueChange = { description = it },
//                                label = { Text("Description") },
//                                modifier = Modifier.fillMaxWidth()
//                            )



                            if (errorMessage != null) {
                                Text(
                                    errorMessage!!,
                                    color = MaterialTheme.colors.error,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }

                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
                            }

                            Button(
                                onClick = {
                                    if (communityId.isNullOrBlank()) {
                                        return@Button
                                    }
                                    if (name.isBlank()) {
                                        errorMessage = "All fields must be filled!"
                                    } else {
                                        errorMessage = null
                                        isLoading = true
                                        viewModel.createVoice(communityId!!.toLong(), name, workingDirectory) {
                                            isLoading = false
                                            if (error == null) {
                                                println("Closing...")
                                                onClose()
                                            } else {
                                                errorMessage = error
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Text("Create")
                            }

                            TextButton(
                                onClick = onClose,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Cancel")
                            }
                        }
                    }
                }
            }
        }
    }
}