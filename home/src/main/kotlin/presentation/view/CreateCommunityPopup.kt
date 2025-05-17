package presentation.view

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import data.model.request.SendMessageRequest
import di.homeModule
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform
import presentation.viewmodel.HomeViewModel
import session.SessionManager

class CreateCommunityPopup {
    @Composable
    fun CreateCommunityPopup(viewModel: HomeViewModel, onClose: () -> Unit) {
        var name by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var isPublic by remember { mutableStateOf(true) }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        var isLoading by remember { mutableStateOf(false) }

        var expanded by remember { mutableStateOf(false) }

        var textFieldSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }

        var error by viewModel.errorMessage
        //var voiceServers by viewModel.voiceServers
        var voiceServers = mapOf(Pair("Russia", listOf("Moscow", "Vladimir")), Pair("USA", listOf("NYC")))

        var selectedVoiceRegion by remember { mutableStateOf("") }
        var selectedVoiceName by remember { mutableStateOf("") }


//        val isCreated = viewModel.isCreated.value
//
//        if (isCreated) {
//            onClose()
//        }

        LaunchedEffect(key1 = "fetchCommunities") {
            println("LaunchedEffect: Calling fetchCommunities")
            try {
                viewModel.fetchVoiceServers()
            } catch (e: Exception) {
                println("LaunchedEffect: Error while calling fetchCommunities: ${e.message}")
            }
        }

        Window(
            onCloseRequest = onClose,
            state = rememberWindowState().apply {
                placement = WindowPlacement.Floating
            },
            title = "Create Community"
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
                            "Create Community",
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
                                label = { Text("Community Name") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            TextField(
                                value = description,
                                onValueChange = { description = it },
                                label = { Text("Description") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Column {
                                OutlinedTextField(
                                    value = "$selectedVoiceRegion - $selectedVoiceName",
                                    onValueChange = {},
                                    label = { Text("Voice Name") },
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
                                    voiceServers.keys.forEach { region ->
                                        voiceServers[region]?.forEach { name ->
                                            DropdownMenuItem(onClick = {
                                                selectedVoiceName = name
                                                selectedVoiceRegion = region
                                                expanded = false
                                            }) {
                                                Text("$region - $name")
                                            }
                                        }
                                    }
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isPublic,
                                    onCheckedChange = { isPublic = it }
                                )
                                Text("Public Community")
                            }



                            if (errorMessage != null) {
                                Text(
                                    errorMessage!!,
                                    color = MaterialTheme.colors.error,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }

//                            if (creationError != null) {
//                                Text(
//                                    creationError,
//                                    color = MaterialTheme.colors.error,
//                                    modifier = Modifier.padding(top = 8.dp)
//                                )
//                            }

                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
                            }

                            Button(
                                onClick = {
                                    if (name.isBlank() || description.isBlank()
                                        || selectedVoiceName.isBlank() || selectedVoiceRegion.isBlank()) {
                                        errorMessage = "All fields must be filled!"
                                    } else {
                                        errorMessage = null
                                        isLoading = true
                                        viewModel.createCommunity(name, description, isPublic,
                                            selectedVoiceRegion, selectedVoiceName) {
                                            isLoading = false
                                            println(error)
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


//fun main() = application {
//    startKoin {
//        modules(homeModule)
//    }
//    val a  = KoinPlatform.getKoin().get<HomeViewModel>()
//    //a.fetchCommunities()
////
////    Window(onCloseRequest = ::exitApplication, title = "Frame") {
////        MaterialTheme {
////            CreateCommunityPopup().CreateCommunityPopup(KoinPlatform.getKoin().get<HomeViewModel>(), {})
////        }
////    }
//
//    CreateCommunityPopup().CreateCommunityPopup(KoinPlatform.getKoin().get<HomeViewModel>(), {})
//
//}
//
