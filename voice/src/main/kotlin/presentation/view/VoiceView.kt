package presentation.view

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import di.voiceModule
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform.getKoin
import presentation.viewmodel.VoiceViewModel
import kotlin.math.sin

class VoiceView {
    @Composable
    fun VoiceView(
        viewModel: VoiceViewModel,
        voiceId: Long,
        onLeaveClick: () -> Unit
    ) {
        LaunchedEffect("once") {
            viewModel.joinVoice(voiceId) {}
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212)) // тёмный фон
        ) {
            VoiceWaveAnimation(modifier = Modifier.fillMaxSize())

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
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            ) {
                Text("Leave", color = Color.White)
            }
        }
    }

    @Composable
    fun VoiceWaveAnimation(modifier: Modifier = Modifier) {
        val infiniteTransition = rememberInfiniteTransition(label = "wave")

        // Плавно меняющиеся амплитуды для нескольких волн
        val amplitude1 by infiniteTransition.animateFloat(
            initialValue = 20f,
            targetValue = 40f,
            animationSpec = infiniteRepeatable(
                animation = tween(2500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "amplitude1"
        )
        val amplitude2 by infiniteTransition.animateFloat(
            initialValue = 10f,
            targetValue = 30f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "amplitude2"
        )
        val amplitude3 by infiniteTransition.animateFloat(
            initialValue = 5f,
            targetValue = 15f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "amplitude3"
        )

        val phase by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 2 * Math.PI.toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "phase"
        )

        Canvas(modifier = modifier) {
            val width = size.width
            val height = size.height / 2

            val path = Path()
            path.moveTo(0f, height)

            val freq1 = 0.02f
            val freq2 = 0.05f
            val freq3 = 0.11f

            for (x in 0..width.toInt()) {
                // Суммируем три синусоиды с разными частотами и амплитудами
                val y = height +
                        sin((x * freq1) + phase) * amplitude1 +
                        sin((x * freq2) + phase * 1.5f) * amplitude2 +
                        sin((x * freq3) + phase * 0.5f) * amplitude3

                path.lineTo(x.toFloat(), y)
            }

            drawPath(
                path = path,
                color = Color.Cyan,
                style = Stroke(width = 4f)
            )
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
