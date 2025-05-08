package presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import data.model.request.CreateChatRequest
import data.model.request.CreateDirectoryRequest
import data.model.request.SendMessageRequest
import data.model.response.ChatDto
import data.usecase.*
import dto.ChatEntity
import dto.CommunityEntity
import dto.MessageEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import session.SessionManager

class HomeViewModel(
    private val fetchCommunitiesUseCase: FetchCommunitiesUseCase,
    private val fetchMyCommunitiesUseCase: FetchMyCommunitiesUseCase,
    private val joinCommunityUseCase: JoinCommunityUseCase,
    private val leaveCommunityUseCase: LeaveCommunityUseCase,
    private val createChatUseCase: CreateChatUseCase,
    private val createDirectoryUseCase: CreateDirectoryUseCase,
    private val deleteChatUseCase: DeleteChatUseCase,
    private val getAllChatsUseCase: GetAllChatsUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val getAllMessagesUseCase: GetAllMessagesUseCase,
    private val updateChatUseCase: UpdateChatUseCase
) {

    private val viewModelScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    var communities = mutableStateOf<List<CommunityEntity>>(emptyList())
        private set

    var myCommunities = mutableStateOf<List<CommunityEntity>>(emptyList())
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    var communitiesState = mutableStateOf<List<CommunityEntity>>(emptyList())
        private set

    var chats = mutableStateOf<List<ChatEntity>>(emptyList())
        private set

    var messages = mutableStateOf<List<MessageEntity>>(emptyList())
        private set

    // присоединиться/покинуть сообщество
    fun toggleJoin(communityId: Long) {
        if (myCommunities.value.any { it.id == communityId }) {
            leaveCommunity(communityId) {
                fetchMyCommunities()
            }
        } else {
            joinCommunity(communityId) {
                fetchMyCommunities()
            }
        }
        fetchMyCommunities()
       // communitiesState.value = updated
    }


    fun fetchCommunities(search: String = "", pageOffset: Long = 0, pageCount: Long = 50) {
        fetchCommunitiesUseCase.execute(
            token = getToken(),  //// INSERT!!!!!!!!!!!!
            name = search,
            pageOffset = pageOffset,
            pageCount = pageCount
        ) { data, error ->
            viewModelScope.launch(Dispatchers.Default) {
                if (data != null) {
                    communities.value = data
                    errorMessage.value = null
                } else {
                    errorMessage.value = error
                }
            }
        }
    }

    fun fetchMyCommunities() {
        fetchMyCommunitiesUseCase.execute(
            token = getToken(),  //// INSERT!!!!!!!!!!!!
        ) { data, error ->
            viewModelScope.launch(Dispatchers.Default) {
                if (data != null) {
                    myCommunities.value = data
                    errorMessage.value = null
                } else {
                    errorMessage.value = error
                }
            }
        }
    }

    fun joinCommunity(communityId: Long, callback: () -> Unit) {
        joinCommunityUseCase.execute(
            token = getToken(),  //// INSERT!!!!!!!!!!!!
            communityId = communityId
        ) { error ->
            viewModelScope.launch(Dispatchers.Default) {
                errorMessage.value = error
                callback()
            }
        }
    }

    fun leaveCommunity(communityId: Long, callback: () -> Unit) {
        leaveCommunityUseCase.execute(
            token = getToken(),  //// INSERT!!!!!!!!!!!!
            communityId = communityId
        ) { error ->
            viewModelScope.launch(Dispatchers.Default) {
                errorMessage.value = error
                callback()
            }
        }
    }


    fun createChat(communityId: Long, chat: CreateChatRequest, callback: () -> Unit) {
        createChatUseCase.execute(
            token = getToken(),  //// INSERT!!!!!!!!!!!!
            communityId = communityId,
            chat = chat
        ) { error ->
            viewModelScope.launch(Dispatchers.Default) {
                errorMessage.value = error
                callback()
            }
        }
    }

    fun getToken(): String {
        return SessionManager.token ?: ""
    }

    fun createDirectory(communityId: Long, directory: CreateDirectoryRequest, callback: () -> Unit) {
        createDirectoryUseCase.execute(
            token = getToken(),  //// INSERT!!!!!!!!!!!!
            communityId = communityId,
            directory = directory
        ) { error ->
            viewModelScope.launch(Dispatchers.Default) {
                errorMessage.value = error
                callback()
            }
        }
    }

//    fun deleteChat(communityId: Long, callback: () -> Unit) {
//        deleteChatUseCase.execute(
//            token = getToken(),  //// INSERT!!!!!!!!!!!!
//            communityId = communityId
//        ) { error ->
//            viewModelScope.launch(Dispatchers.Default) {
//                errorMessage.value = error
//                callback()
//            }
//        }
//    }

    fun getAllChats(communityId: Long, callback: () -> Unit) {
        getAllChatsUseCase.execute(
            token = getToken(),  //// INSERT!!!!!!!!!!!!
            communityId = communityId
        ) { data, error ->
            viewModelScope.launch(Dispatchers.Default) {
                if (data != null) {
                    chats.value = data
                    errorMessage.value = null
                } else {
                    errorMessage.value = error
                }

                callback()
            }
        }
    }

    fun sendMessage(communityId: Long, chatId: Long, message: SendMessageRequest, callback: () -> Unit) {
        sendMessageUseCase.execute(
            token = getToken(),  //// INSERT!!!!!!!!!!!!
            chatId = communityId,
            messageDto = message,
        ) { error ->
            viewModelScope.launch(Dispatchers.Default) {
                errorMessage.value = error
                callback()
            }
        }
    }

    fun getAllMessages(chatId: Long, page: Long, size: Long, callback: () -> Unit) {
        getAllMessagesUseCase.execute(
            token = getToken(),  //// INSERT!!!!!!!!!!!!
            chatId = chatId,
            page = page,
            size = size
        ) { data, error ->
            viewModelScope.launch(Dispatchers.Default) {
                if (data != null) {
                    messages.value = data
                    errorMessage.value = null
                } else {
                    errorMessage.value = error
                }
                errorMessage.value = error
                callback()
            }
        }
    }

}
