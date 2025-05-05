package presentation.view

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import di.homeModule
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform
import presentation.viewmodel.HomeViewModel

class HomeView {
    @Composable
    fun HomeScreen(
        viewModel: HomeViewModel,
        onSearchClick: () -> Unit = {},
        onProfileClick: () -> Unit = {}
    ) {
        println("HomeScreen: Composable function is called")

        var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
        var selectedCommunity by remember { mutableStateOf("") }
        var serverError by remember { mutableStateOf<String?>(null) }

        val myCommunities by viewModel.myCommunities
        val yourStats = emptyList<String>()

        val communities by viewModel.communities
        val error by viewModel.errorMessage

        LaunchedEffect(key1 = "fetchCommunities") {
            println("LaunchedEffect: Calling fetchCommunities")
            try {
                viewModel.fetchCommunities()
                viewModel.fetchMyCommunities()

            } catch (e: Exception) {
                println("LaunchedEffect: Error while calling fetchCommunities: ${e.message}")
            }
        }

        Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {

            // Top-right Profile button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                // Title in frame
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center

                ) {
                    Text("Your communities")
                }
                Spacer(modifier = Modifier.width(8.dp))

                // Title "Find public communities"
                Box(
                    modifier = Modifier
                        .weight(3f)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center

                ) {
                    Text("Find public communities")
                }
                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onSearchClick,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFDFFFE0))
                ) {
                    Text("S", color = Color.Black)
                }
                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onProfileClick,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFFE0E0))
                ) {
                    Text("P", color = Color.Black)
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxSize()) {
                // --- Left panel ---
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {

                    // Community list
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(1.dp, Color.Gray)
                            .padding(4.dp)
                    ) {
                        myCommunities.forEachIndexed { index, it ->
                            val isSelected = it.name == selectedCommunity
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (isSelected) Color.LightGray else Color.Transparent)
                                    .clickable {
                                        selectedCommunity = it.name
                                        serverError = null
                                    }
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(it.name)
                                Text(yourStats.getOrNull(index) ?: "")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // --- Right panel ---
                Column(modifier = Modifier.weight(3f)) {

                    // Search bar + Search button
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text("Search...") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = onSearchClick,
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFDFFFE0))
                        ) {
                            Text("S", color = Color.Black)
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(1.dp, Color.Gray)
                            .padding(12.dp)
                    ) {
                        communities.forEach { community ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(community.name, style = MaterialTheme.typography.h6)
                                        Text(community.description ?: "", style = MaterialTheme.typography.body2)
                                    }

                                    Button(
                                        onClick = {
                                            viewModel.toggleJoin(community.id)
                                        }
                                    ) {
                                        Text(if (myCommunities.any {it.id == community.id}) "Leave" else "Join")
                                    }
                                }
                            }
                        }

                    }

//                    // Community info block
//                    Column(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .border(1.dp, Color.Gray)
//                            .padding(12.dp)
//                    ) {
//                        Text(selectedCommunity, fontSize = 18.sp, color = Color.Blue)
//                        Spacer(modifier = Modifier.height(4.dp))
//                        Text(
//                            if (selectedCommunity == "HSE")
//                                "This is a community of administrators of the frame app"
//                            else
//                                "",
//                            fontSize = 14.sp
//                        )
//                    }
                }
            }

            // Server Error block
            if (serverError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFC0C0))
                        .padding(8.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(serverError!!, color = Color.Red, fontSize = 14.sp)
                }
            }
        }
    }
}

fun main() = application {
    startKoin {
        modules( homeModule)
    }
    val a  = KoinPlatform.getKoin().get<HomeViewModel>()
    a.fetchCommunities()

    Window(onCloseRequest = ::exitApplication, title = "Frame") {
        MaterialTheme {
            HomeView().HomeScreen(
                KoinPlatform.getKoin().get<HomeViewModel>(),
                onSearchClick = { println("Navigate to Home/Search") },
                onProfileClick = { println("Navigate to Profile") }
            )
        }
    }
}
