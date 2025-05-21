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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import di.voiceModule
import org.koin.core.context.startKoin
import presentation.viewmodel.VoiceViewModel
import java.awt.Window
import kotlin.math.sin
import kotlin.math.PI



class VoiceView {
    @Composable
    fun VoiceView(
        viewModel: VoiceViewModel,
        voiceId: Long,
        onLeaveClick: () -> Unit
    ) {
        val connectionGuide by viewModel.connectionGuide

        LaunchedEffect("once") {
            viewModel.joinVoice(voiceId) {
                // TODO: after connection
            }
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
        val phase by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 2 * Math.PI.toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "wavePhase"
        )

        Canvas(modifier = modifier) {
            val width = size.width
            val height = size.height / 2

            val path = Path()
            val amplitude = 40f
            val frequency = 0.05f

            path.moveTo(0f, height)

            for (x in 0..width.toInt()) {
                val y = height + sin((x * frequency) + phase) * amplitude
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