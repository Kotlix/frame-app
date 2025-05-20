package presentation.view

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import data.model.request.SendMessageRequest
import di.homeModule
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform
import presentation.viewmodel.HomeViewModel
import presentation.viewmodel.ProfileViewModel
import session.SessionManager
import session.client.handler.ServerPacketFilter
import session.client.handler.ServerPacketListenerWatcher


class ProfilePopup {
    @Composable
    fun ProfilePopup(viewModel: ProfileViewModel, onClose: () -> Unit) {
            Window(
                onCloseRequest = {},
                title = "Profile Info",
                alwaysOnTop = true,
                undecorated = true
            ) {
                var isEditingUserName by remember { mutableStateOf(false) }
                var isEditingEmail by remember { mutableStateOf(false) }
                var isEditingPassword by remember { mutableStateOf(false) }

                val profile by viewModel.profile

                //var email by viewModel.profile?.email
//                var password by remember { mutableStateOf("") }
//                var username by remember { mutableStateOf(profile?.username ?: "") }

                var newEmail by remember { mutableStateOf("") }
                var newPassword by remember { mutableStateOf("[hidden]") }
                var newUserName by remember { mutableStateOf("") }

                var showVerifyEmailPopup by remember { mutableStateOf(false) }
                var showVerifyUserNamePopup by remember { mutableStateOf(false) }
                var showVerifyPasswordPopup by remember { mutableStateOf(false) }

                var verifyOperation by remember { mutableStateOf<((String) -> Unit)?>(null) }

                fun resetValues() {
                    if (profile != null) {
                        newEmail = profile!!.email
                        newUserName = profile!!.username
                        newPassword = "[hidden]"
                    }
                }

                LaunchedEffect(profile) {
                    viewModel.getMyProfileInfo {  }
                    resetValues()
                }

                if (showVerifyEmailPopup) {
                    viewModel.changeEmail(newEmail) {

                    }
                    ProfileCodeVerifyPopup().CodeVerifyPopup(
                        viewModel = viewModel,
                        verifyMethod = { secret -> viewModel.changeEmailApply(secret) {} },
                        onClose = {
                            showVerifyEmailPopup = false
                            onClose()
                        }
                    )

                }

                if (showVerifyPasswordPopup) {
                    viewModel.changePassword(newPassword) {

                    }

                    ProfileCodeVerifyPopup().CodeVerifyPopup(
                        viewModel = viewModel,
                        verifyMethod = { secret -> viewModel.changePasswordApply(secret) {} },
                        onClose = {
                            showVerifyPasswordPopup = false
                            onClose()
                        }
                    )

                }

                if (showVerifyUserNamePopup) {
                    viewModel.changeUserName(newUserName) {

                    }

                    ProfileCodeVerifyPopup().CodeVerifyPopup(
                        viewModel = viewModel,
                        verifyMethod = { secret -> viewModel.changeUserNameApply(secret) {} },
                        onClose = {
                            showVerifyUserNamePopup = false
                            onClose()
                        }
                    )

                }

                Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
//                    Text("ID: ${profile?.id}", style = MaterialTheme.typography.body1)
//                    Spacer(Modifier.height(8.dp))

                    Row {
                        LabeledField("Username", newUserName, isEditingUserName) {
                            newUserName = it
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        if (!isEditingUserName) {
                            Button(onClick = {
                                resetValues()
                                isEditingUserName = true
                                isEditingEmail = false
                                isEditingPassword = false
                            }) {
                                Text("Edit")
                            }
                        } else {
                            Button(onClick = {
                                isEditingUserName = false
                                isEditingEmail = false
                                isEditingPassword = false
                                showVerifyUserNamePopup = true
                            }) {
                                Text("Save")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = {
                                resetValues()
                                isEditingUserName = false
                                isEditingEmail = false
                                isEditingPassword = false
                            }) {
                                Text("Cancel")
                            }
                        }
                    }

                    Row {
                        LabeledField("Email", newEmail, isEditingEmail) {
                             newEmail = it
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        if (!isEditingEmail) {
                            Button(onClick = {
                                resetValues()
                                isEditingUserName = false
                                isEditingEmail = true
                                isEditingPassword = false
                            }) {
                                Text("Edit")
                            }
                        } else {
                            Button(onClick = {
                                isEditingUserName = false
                                isEditingEmail = false
                                isEditingPassword = false
                                showVerifyEmailPopup = true
                            }) {
                                Text("Save")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = {
                                resetValues()
                                isEditingUserName = false
                                isEditingEmail = false
                                isEditingPassword = false
                            }) {
                                Text("Cancel")
                            }
                        }
                    }

                    Row {
                        LabeledField("Password", newPassword, isEditingPassword) {
                            newPassword = it
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        if (!isEditingPassword) {
                            Button(onClick = {
                                resetValues()
                                newPassword = ""
                                isEditingUserName = false
                                isEditingEmail = false
                                isEditingPassword = true
                            }) {
                                Text("Edit")
                            }
                        } else {
                            Button(onClick = {
                                isEditingUserName = false
                                isEditingEmail = false
                                isEditingPassword = false
                                showVerifyPasswordPopup = true
                            }) {
                                Text("Save")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = {
                                resetValues()
                                isEditingUserName = false
                                isEditingEmail = false
                                isEditingPassword = false
                            }) {
                                Text("Cancel")
                            }
                        }
                    }

                    Row {
                        Spacer(Modifier.height(16.dp))

                        //if (!isEditing) {
//                        Button(onClick = {  }) {
//                            Text("Edit")
//                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = { onClose() }) {
                            Text("Exit")
                        }
//                        } else {
//                            LabeledField("New password", "", isEditing) {
//
//                            }
//                            Spacer(Modifier.height(16.dp))
//
//                            Button(onClick = {
//                                isEditing = false
//                                // Here you can add logic to persist changes
//                                //println("Saved: ${profile?.username, $username, $email")
//                            }) {
//                                Text("Save Changes")
//                            }
//                            Spacer(modifier = Modifier.width(8.dp))
//                            Button(onClick = { isEditing = true }) {
//                                Text("Cancel")
//                            }
//                        }
                    }
                }
            }
    }

    @Composable
    fun LabeledField(label: String, value: String, isEditable: Boolean, onValueChange: (String) -> Unit) {
        Column {
            Text(label, style = MaterialTheme.typography.h4)
            if (isEditable) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange
                )
            } else {
                Text(value, style = MaterialTheme.typography.body1)
            }
            Spacer(Modifier.height(8.dp))
        }
    }

}