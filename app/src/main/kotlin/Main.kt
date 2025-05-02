import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import di.initKoin
import org.koin.mp.KoinPlatform.getKoin
import presentation.view.AuthView
import presentation.viewmodel.AuthViewModel


fun main()  = application {
    initKoin()
    Window(
        onCloseRequest = ::exitApplication,
        title = "Frame",
        state = rememberWindowState().apply {
            placement = WindowPlacement.Maximized
        }) {
        MaterialTheme {
            val viewModel: AuthViewModel = getKoin().get()
            AuthView().AuthApp(viewModel = viewModel)
        }
    }
}
