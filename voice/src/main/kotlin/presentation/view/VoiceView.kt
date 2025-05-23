package presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import di.voiceModule
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform.getKoin
import presentation.viewmodel.VoiceViewModel
import voice.VoiceManager

class VoiceView {
    @Composable
    fun VoiceView(
        viewModel: VoiceViewModel,
        voiceId: Long,
        onLeaveClick: () -> Unit
    ) {
        var isMuted by mutableStateOf(VoiceManager.audioService.isInputMuted)

        var attendants by viewModel.attendants

        LaunchedEffect("once") {
            viewModel.joinVoice(voiceId) {
                VoiceManager.voiceClient.bindAttendantsCallback {
                    attendants = it
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212)) // тёмный фон
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(Color(0xFF121212))
                    .padding(bottom = 32.dp)
            ) {
                attendants.forEach { (id, timestamp) ->
                    val speaking = System.currentTimeMillis() - timestamp < 1_000
                    var username by remember { mutableStateOf("unknown") }
                    viewModel.findUsername(id) {
                        username = it
                    }
                    Box(
                        modifier = Modifier
                            .border(2.dp, if (speaking) Color.Green else Color.Gray, shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .background(Color.Transparent) // или цвет фона, если нужно
                    ) {
                        Text(
                            text = username,
                            color = Color.White
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(Color(0xFF202020))
                    .padding(bottom = 32.dp)
            ) {
                Button(
                    onClick = {
                        val newState = !isMuted
                        VoiceManager.audioService.isInputMuted = newState
                        isMuted = newState
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor =
                            if (isMuted) Color.Red
                            else Color.Green
                    ),
                    modifier = Modifier
                        .padding(bottom = 32.dp)
                ) {
                    val text =
                        if (isMuted) "Muted"
                        else "Speak"
                    Text(text, color = Color.White)
                }

                Button(
                    onClick = {
                        viewModel.leaveVoice(voiceId) {
                            onLeaveClick()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Blue
                    ),
                    modifier = Modifier
                        .padding(bottom = 32.dp)
                ) {
                    Text("Leave", color = Color.White)
                }
            }
        }
    }
}


fun main() = application {
    // Запуск Koin
    startKoin {
        modules(voiceModule)
    }

    // Запуск окна
    Window(onCloseRequest = ::exitApplication, title = "Voice View") {
        val view = VoiceView()
        view.VoiceView(getKoin().get<VoiceViewModel>(), -1L, onLeaveClick = {
            println("Leave clicked!")
        })
    }
}
