import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import di.initKoin
import org.koin.mp.KoinPlatform.getKoin
import presentation.view.AuthView
import presentation.view.HomeView
import presentation.viewmodel.AuthViewModel
import presentation.viewmodel.HomeViewModel


fun main() {
    initKoin()
    val authViewModel: AuthViewModel = getKoin().get()
    val homeViewModel = getKoin().get<HomeViewModel>()

    application {
        var loggedInState by remember { mutableStateOf(false) }

        Window(
            onCloseRequest = ::exitApplication,
            title = "Frame",
            state = rememberWindowState().apply {
                placement = WindowPlacement.Maximized
            }
        ) {
            MaterialTheme {
                if (!loggedInState) {
                    println("AUTH")
                    AuthView().AuthApp(
                        viewModel = authViewModel,
                        callback = { loggedInState = true }
                    )
                } else {
                    println("HOME")
                    HomeView().HomeView(viewModel = homeViewModel)
                }
            }
        }
    }
}
