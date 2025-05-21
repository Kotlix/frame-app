package presentation.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.rememberWindowState
import dto.ChatEntity
import dto.VoiceEntity
import presentation.viewmodel.HomeViewModel

class UpdateVoicePopup {
    @Composable
    fun UpdateVoicePopup(viewModel: HomeViewModel, voice: VoiceEntity, onClose: () -> Unit) {
        var newVoiceName by remember { mutableStateOf("") }
        var directories by viewModel.directories
        var errorMessage by remember { mutableStateOf<String?>(null) }
        var isLoading by remember { mutableStateOf(false) }
        var expanded by remember { mutableStateOf(false) }
        var selectedDirectoryId by remember { mutableStateOf<Long>(-1) }
        var selectedDirectoryName by remember { mutableStateOf<String?>(directories.find { it.id == voice.directoryId }?.name) }
        var textFieldSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }


        var error by viewModel.errorMessage


        Window(
            onCloseRequest = onClose,
            state = rememberWindowState().apply {
                placement = WindowPlacement.Floating
            },
            title = "Update Voice"
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
                            "Update Voice",
                            style = MaterialTheme.typography.h4,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        Column(
                            modifier = Modifier.widthIn(max = 400.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            TextField(
                                value = newVoiceName,
                                onValueChange = { newVoiceName = it },
                                label = { Text("New Voice Name") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Column {

                                OutlinedTextField(
                                    value = selectedDirectoryName ?: "",
                                    onValueChange = {},
                                    label = { Text("Directory Name") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .onGloballyPositioned { coordinates ->
                                            textFieldSize = coordinates.size.toSize()
                                        }
                                        .clickable { expanded = true },
                                    enabled = false,
                                    trailingIcon = {
                                        IconButton(onClick = { expanded = !expanded }) {
                                            Icon(
                                                imageVector = if (expanded) Icons.Default.ArrowDropDown else Icons.Default.ArrowDropDown,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                )

                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    modifier = Modifier
                                        .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
                                ) {
                                    directories.forEach { dir ->
                                        DropdownMenuItem(onClick = {
                                            selectedDirectoryId = dir.id
                                            expanded = false
                                        }) {
                                            Text(dir.name)
                                        }
                                    }
                                }
                            }

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
                                    errorMessage = null
                                    isLoading = true
                                    viewModel.updateVoice(
                                        voice.id,
                                        newVoiceName.ifBlank { voice.name },
                                        if (selectedDirectoryId == -1L) voice.directoryId else selectedDirectoryId
                                    ) {
                                        isLoading = false
                                        if (error == null) {
                                            println("Closing...")
                                            onClose()
                                        } else {
                                            errorMessage = error
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Text("Update")
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