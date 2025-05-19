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
import session.SessionManager
import session.client.handler.ServerPacketFilter
import session.client.handler.ServerPacketListenerWatcher

class HomeView {
    @Composable
    fun HomeScreen(
        viewModel: HomeViewModel,
        onSearchClick: () -> Unit = {},
        onCommunityClick: () -> Unit = {},
        onProfileClick: () -> Unit = {}
    ) {
        println("HomeScreen: Composable function is called")

        var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
        var showCreatePopup by remember { mutableStateOf(false) }
        var selectedCommunityId by viewModel.selectedCommunityId
        var serverError by viewModel.errorMessage

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
                    onClick = {
                        showCreatePopup = true
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0x34dbebe0))
                ) {
                    Text("Create Community")
                }

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
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(1.dp, Color.Gray)
                            .padding(4.dp)
                    ) {
                        items(myCommunities) {
                            val isSelected = it.id.toString() == selectedCommunityId
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (isSelected) Color.LightGray else Color.Transparent)
                                    .clickable {
                                        selectedCommunityId = it.id.toString()
                                        serverError = null
                                        onCommunityClick()
                                    }
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(it.name)
                                //Text(yourStats.getOrNull(index) ?: "")
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
                            onValueChange = { searchQuery = it
                                viewModel.fetchCommunities(search = it.text)
                            },
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

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(1.dp, Color.Gray)
                            .padding(12.dp)
                    ) {
                        items(communities) { community ->
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

                                    if (!myCommunities.any {it.id == community.id}) {
                                        Button(
                                            onClick = {
                                                viewModel.toggleJoin(community.id)
                                            }
                                        ) {
                                            Text( "Join")
                                        }
                                    } else {
                                        Button(
                                            onClick = { },
                                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xE0E0E0E0))
                                        ) {
                                            Text( "Joined", color = Color.White)
                                        }
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

            // Показываем окно, если showCreatePopup = true
            if (showCreatePopup) {
                CreateCommunityPopup().CreateCommunityPopup(viewModel) {
                    showCreatePopup = false
                    viewModel.fetchMyCommunities()
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

    @Composable
    fun CommunityScreen(viewModel: HomeViewModel) {

        var selectedCommunityId by viewModel.selectedCommunityId
        var selectedChatId by viewModel.selectedChatId

        var serverError by remember { mutableStateOf<String?>(null) }

        val myCommunities by viewModel.myCommunities
        val yourStats = emptyList<String>()

        val communities by viewModel.communities
        val chats by viewModel.chats
        val messages by viewModel.messages
        val directories by viewModel.directories
        val myProfile by viewModel.profile
        val members by viewModel.members
        val membersState by viewModel.membersState
        val idUserNameMap by viewModel.idUserNameMap
        val error by viewModel.errorMessage

        val messageText = remember { mutableStateOf("") }

        var toggleNotification by remember { mutableStateOf<Boolean>(false) }
        var kafkaId by remember { mutableStateOf<Long>(0) }

        LaunchedEffect(selectedCommunityId) {
            try {
                viewModel.fetchCommunities()
                viewModel.fetchMyCommunities()
                viewModel.getMyProfileInfo{ }
                viewModel.getAllDirectories(selectedCommunityId!!.toLong(), {})
                viewModel.getAllChats(selectedCommunityId!!.toLong(), {})
                viewModel.getAllVoices(selectedCommunityId!!.toLong(), {})
                viewModel.getMembers(selectedCommunityId!!.toLong()) {
                    viewModel.getUserNameMap {  }
                }

            } catch (e: Exception) {
                println("LaunchedEffect: Error while calling fetchCommunities: ${e.message}")
            }
        }

        fun delete() {
            SessionManager.sessionClient.getPacketListener().remove(kafkaId)
        }

        LaunchedEffect(selectedChatId to toggleNotification) {
            selectedChatId?.let {
                viewModel.getAllMessages(selectedChatId!!.toLong(), callback = { })
                SessionManager.sessionClient.getPacketListener()
                delete()
                kafkaId = SessionManager.sessionClient.getPacketListener().register(ServerPacketFilter { it.hasMessageNotify() },
                    ServerPacketListenerWatcher.Once) {
                    toggleNotification = !toggleNotification
                }
            }
        }

        Row(modifier = Modifier.fillMaxSize().padding(4.dp)) {

            // --- Left panel ---
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {

                // Community list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(1.dp, Color.Gray)
                        .padding(4.dp)
                ) {
                    items(myCommunities) { myComm ->
                        val isSelected = myComm.id.toString() == selectedCommunityId
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isSelected) Color.LightGray else Color.Transparent)
                                .clickable {
                                    selectedCommunityId = myComm.id.toString()
                                    serverError = null
                                }
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(myComm.name)
                            //Text(yourStats.getOrNull ?: "")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Средний столбец: элементы и чат

            Column(modifier = Modifier.weight(2f).fillMaxHeight()) {
//
//                Button(onClick = {
//                    CreateCommunityPopup().CreateCommunityPopup(viewModel) {
//                        viewModel.fetchMyCommunities()
//                    }
//                }, modifier = Modifier.width(40.dp)) { Text("Create community") }


                // Верхняя панель (elements, chat)
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFFEFEFEF))
                            .padding(4.dp)
                    ) {
                        Text("elements")
                    }
                    Box(
                        modifier = Modifier
                            .weight(2f)
                            .background(Color(0xFFA6E3A1))
                            .padding(4.dp)
                    ) {
                        Text("chat")
                    }
                }

                Row(modifier = Modifier.fillMaxSize()) {
                    // Элементы
                    LazyColumn(
                        modifier = Modifier
                            .weight(0.5f)
                            .fillMaxHeight()
                            .border(1.dp, Color.Gray)
                            .padding(4.dp)
                    ) {
                        directories.forEach { dir ->
                            item {
                                Column {
                                    Text("📁 ${dir.name}", modifier = Modifier
                                        .background(Color(0xFFDEEFFF))
                                        .padding(4.dp)
                                        .clickable { }
                                    )

                                    Text("chats", modifier = Modifier
                                        .background(Color(0xFFDEEFFF))
                                        .padding(start = 10.dp, top = 4.dp, bottom = 4.dp)
                                        .clickable { }
                                    )

                                        val filteredChats = chats.filter { it.directoryId == dir.id }
                                        filteredChats.forEach { chat ->
                                           // println("💬 ${chat.name}")
                                            Text(
                                                "💬 ${chat.name}",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(start = 16.dp, top = 2.dp, bottom = 2.dp)
                                                    .clickable {
                                                        selectedChatId = chat.id.toString()
                                                        println("💬 ${selectedChatId}")
                                                    },
                                                color = Color.DarkGray
                                            )
                                        }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Button(onClick = {}, modifier = Modifier.width(40.dp)) { Text("+") }

                                    Text("voices", modifier = Modifier
                                        .background(Color(0xFFDEEFFF))
                                        .padding(start = 10.dp, top = 4.dp, bottom = 4.dp)
                                        .clickable { }
                                    )

//                            val filteredVoiceChats = chats.filter { it.directoryId == dir.directoryId }
//                            items(filteredVoiceChats) { chat ->
//                                Text(
//                                    "🎙 ${chat.name}",
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .padding(start = 16.dp, top = 2.dp, bottom = 2.dp)
//                                        .clickable { /* onChatClick(chat.id) */ },
//                                    color = Color.DarkGray
//                                )
//                            }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Button(onClick = {}, modifier = Modifier.width(40.dp)) { Text("+") }
                                }
                            }
                        }
//                        Text("📁 root", modifier = Modifier.background(Color(0xFFDEEFFF)).padding(4.dp))
//                        Text("💬 chat", modifier = Modifier.background(Color(0xFFA6E3A1)).padding(4.dp))
//                        Text("🎙 voice", modifier = Modifier.background(Color(0xFFB4D3F2)).padding(4.dp))
                        item {
                            Column {
                                Spacer(modifier = Modifier.height(4.dp))
                                Button(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("+") }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Чат
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .border(1.dp, Color.Gray)
                            .padding(4.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            reverseLayout = true
                        ) {
                            val currentMessages = messages.filter { it.chatId == selectedChatId?.toLong() }
                            items(currentMessages.sortedBy { it.createdAt }.reversed()) { message ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp),
                                    contentAlignment = if (message.authorId == myProfile?.id) Alignment.CenterEnd else Alignment.CenterStart
                                ) {
                                    if (message.authorId == myProfile?.id) {
                                        MessageBubble(sender = "You", message = message.message)
                                    } else if (!idUserNameMap.containsKey(message.authorId)) {
                                        MessageBubble(sender = "Unknown", message = message.message)
                                    } else {
                                        MessageBubble(
                                            sender = idUserNameMap[message.authorId] ?: "Unknown",
                                            message = message.message
                                        )
                                    }
                                }
                            }
                        }

                        // --------TEXT MESSAGE--------
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            OutlinedTextField(
                                value = messageText.value,
                                onValueChange = { messageText.value = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 56.dp, max = 150.dp)
                                    .verticalScroll(rememberScrollState()),
                                label = { Text("Type your message...") },
                                maxLines = 5
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    if (messageText.value.isNotBlank()) {
                                        println("Sending message: ${messageText.value}")
                                        viewModel.sendMessage(
                                            selectedChatId!!.toLong(),
                                            SendMessageRequest(
                                                messageText.value
                                            ),
                                            {
//                                                viewModel.getAllMessages(chatId = selectedChatId!!.toLong(),
//                                                    page = 0,
//                                                    size = 50,
//                                                    {
//
//                                                    }
//                                                )
                                            }
                                        )
                                        messageText.value = ""
                                    }
                                },
                                modifier = Modifier.align(Alignment.Bottom)
                            ) {
                                Text("Send")
                            }
                        }
                        // Ошибка
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp).background(Color(0xFFFFC0C0)),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text("Server Error 500", color = Color.Red, modifier = Modifier.padding(4.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Правая панель: пользователи
            Column(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxHeight()
                    .border(1.dp, Color.Gray)
                    .padding(4.dp)
            ) {
                Text("users", modifier = Modifier.padding(bottom = 4.dp))
                Text("online", modifier = Modifier.padding(vertical = 2.dp))
                for (userId in members.filter { it != myProfile?.id }.filter {
                    membersState.find { a -> a.userId == it }?.online ?: false
                }
                ) {
                    UserTag(idUserNameMap[userId] ?: "Unknown Name")
                }
                Text("offline", modifier = Modifier.padding(top = 8.dp, bottom = 2.dp))
                for (userId in members.filter { it != myProfile?.id }.filter {
                    val pred = membersState.find { a -> a.userId == it }?.online ?: false
                    !pred
                }) {
                    UserTag(idUserNameMap[userId] ?: "Unknown Name")
                }
                Spacer(modifier = Modifier.height(8.dp))
                //Button(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("+") }
//                Spacer(modifier = Modifier.height(8.dp))
//                RemovableUser("onar")
//                RemovableUser("admin")
            }
        }
    }

    @Composable
    fun MessageBubble(sender: String, message: String) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            if (sender != "You") {
                Text(sender, modifier = Modifier.background(Color(0xFFFFFF00)).padding(horizontal = 6.dp))
                Text(
                    message, modifier = Modifier
                        .padding(start = 16.dp)
                        .background(Color.LightGray)
                        .padding(6.dp)
                )
            } else {
                Text(sender, modifier = Modifier.background(Color(0xFFFFFF00)).padding(horizontal = 6.dp))
                Text(
                    message, modifier = Modifier
                        .padding(start = 16.dp)
                        .background(Color.Blue)
                        .padding(6.dp)
                )
            }
        }
    }

    @Composable
    fun UserTag(name: String, highlight: Boolean = false) {
        Text(
            name,
            modifier = Modifier
                .fillMaxWidth()
                .background(if (highlight) Color(0xFFFFFF88) else Color.Transparent)
                .padding(4.dp)
        )
    }

    @Composable
    fun RemovableUser(name: String) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
        ) {
            Text(name, modifier = Modifier.weight(1f).padding(start = 4.dp))
            Button(onClick = {}, modifier = Modifier.size(24.dp), contentPadding = PaddingValues(0.dp)) {
                Text("X", fontSize = 12.sp)
            }
        }
    }

    @Composable
    fun HomeView(
        viewModel: HomeViewModel
    ) {
        var showCommunityScreen by remember { mutableStateOf(false) }

        val onCommunityClick: () -> Unit = {
            showCommunityScreen = true
        }

        if (showCommunityScreen) {
            HomeView().CommunityScreen(KoinPlatform.getKoin().get<HomeViewModel>())

        } else {
            HomeView().HomeScreen(
                viewModel,
                onSearchClick = { println("Navigate to Home/Search") },
                onCommunityClick = onCommunityClick,
                onProfileClick = { println("Navigate to Profile") }
            )
        }

    }


    fun launchHome() = application {
//    startKoin {
//        modules( homeModule)
//    }
        val a  = KoinPlatform.getKoin().get<HomeViewModel>()
        a.fetchCommunities()

        Window(onCloseRequest = ::exitApplication, title = "Frame") {
            MaterialTheme {
                HomeView().HomeScreen(
                    KoinPlatform.getKoin().get<HomeViewModel>(),
                    onSearchClick = { println("Navigate to Home/Search") },
                    onProfileClick = { println("Navigate to Profile") }
                )
                //HomeView().ChatMockScreen(KoinPlatform.getKoin().get<HomeViewModel>())
            }
        }
    }
}

fun main () {
    startKoin {
        modules(homeModule)
    }
    HomeView().launchHome()
 }

