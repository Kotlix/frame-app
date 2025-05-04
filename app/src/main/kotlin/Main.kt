import androidx.compose.material.MaterialTheme
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
