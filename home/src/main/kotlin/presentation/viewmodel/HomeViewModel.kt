package presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import data.model.request.SendMessageRequest
import data.model.response.ProfileInfo
import data.model.response.UserStateEntity
import data.usecase.chat.CreateChatUseCase
import data.usecase.chat.DeleteChatUseCase
import data.usecase.chat.GetAllChatsUseCase
import data.usecase.chat.UpdateChatUseCase
import data.usecase.community.*
import data.usecase.directory.CreateDirectoryUseCase
import data.usecase.directory.DeleteDirectoryUseCase
import data.usecase.directory.GetAllDirectoriesUseCase
import data.usecase.directory.UpdateDirectoryUseCase
import data.usecase.message.GetAllMessagesUseCase
import data.usecase.message.SendMessageUseCase
import data.usecase.profile.GetMyProfileInfo
import data.usecase.profile.GetProfileInfoUseCase
import data.usecase.server.FetchVoiceServersUseCase
import data.usecase.userstate.GetUserStateUseCase
import data.usecase.voice.CreateVoiceUseCase
import data.usecase.voice.DeleteVoiceUseCase
import data.usecase.voice.GetAllVoicesUseCase
import data.usecase.voice.UpdateVoiceUseCase
import dto.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import session.SessionManager

class HomeViewModel(
    private val fetchCommunitiesUseCase: FetchCommunitiesUseCase,
    private val fetchMyCommunitiesUseCase: FetchMyCommunitiesUseCase,
    private val fetchVoiceServersUseCase: FetchVoiceServersUseCase,
    private val joinCommunityUseCase: JoinCommunityUseCase,
    private val leaveCommunityUseCase: LeaveCommunityUseCase,
    private val createCommunityUseCase: CreateCommunityUseCase,
    private val createChatUseCase: CreateChatUseCase,
    private val createVoiceUseCase: CreateVoiceUseCase,
    private val createDirectoryUseCase: CreateDirectoryUseCase,
    private val deleteChatUseCase: DeleteChatUseCase,
    private val getAllChatsUseCase: GetAllChatsUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val getAllMessagesUseCase: GetAllMessagesUseCase,
    private val updateChatUseCase: UpdateChatUseCase,
    private val getAllDirectoriesUseCase: GetAllDirectoriesUseCase,
    private val getAllVoicesUseCase: GetAllVoicesUseCase,
    private val getMyProfileInfo: GetMyProfileInfo,
    private val getProfileInfoUseCase: GetProfileInfoUseCase,
    private val getMembersUseCase: GetMembersUseCase,
    private val getUserStateUseCase: GetUserStateUseCase,
    private val updateVoiceUseCase: UpdateVoiceUseCase,
    private val updateDirectoryUseCase: UpdateDirectoryUseCase,
    private val deleteVoiceUseCase: DeleteVoiceUseCase,
    private val deleteDirectoryUseCase: DeleteDirectoryUseCase
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val viewModelScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    var communities = mutableStateOf<List<CommunityEntity>>(emptyList())
        private set

    var myCommunities = mutableStateOf<List<CommunityEntity>>(emptyList())
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    var selectedCommunityId = mutableStateOf<String?>(null)
        private set

    var selectedChatId = mutableStateOf<String?>(null)
        private set

    var communitiesState = mutableStateOf<List<CommunityEntity>>(emptyList())
        private set

    var directories = mutableStateOf<List<DirectoryEntity>>(emptyList())
        private set

    var chats = mutableStateOf<List<ChatEntity>>(emptyList())
        private set

    var voices = mutableStateOf<List<VoiceEntity>>(emptyList())
        private set

    var messages = mutableStateOf<List<MessageEntity>>(emptyList())
        private set

    var voiceServers = mutableStateOf<Map<String, List<String>>>(emptyMap())
        private set

    var profile = mutableStateOf<ProfileInfo?>(null)
        private set

    var members = mutableStateOf<List<Long>>(listOf())
        private set

    var membersState = mutableStateOf<List<UserStateEntity>>(listOf())
        private set

    var idUserNameMap = mutableStateOf<Map<Long, String?>>(mapOf())
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

                    try {
                        val communities = data.map { it.id }
                        val success = SessionManager.sessionClient.updateMessageNotificationPreferences(communities)

                        if (success) {
                            logger.info("Notification preferences AGREED: $communities.")
                        } else {
                            logger.warn("Notification preferences DISAGREED: $communities.")
                        }
                    } catch (ex: Exception) {
                        logger.error("Notification preferences push error.", ex)
                    }
                } else {
                    errorMessage.value = error
                }
            }
        }
    }

    fun fetchVoiceServers() {
        fetchVoiceServersUseCase.execute(
            token = getToken()  //// INSERT!!!!!!!!!!!!
        ) { data, error ->
            viewModelScope.launch(Dispatchers.Default) {
                println(data.toString())
                if (data != null) {
                    voiceServers.value = data
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


    fun createChat(communityId: Long, name: String, directoryId: Long, order: Int = 0, callback: () -> Unit) {
        createChatUseCase.execute(
            token = getToken(),  //// INSERT!!!!!!!!!!!!
            communityId = communityId,
            name = name,
            directoryId = directoryId,
            order = order
        ) { data, error ->
            viewModelScope.launch(Dispatchers.Default) {
                errorMessage.value = error
                callback()
            }
        }
    }

    fun createVoice(communityId: Long, name: String, directoryId: Long, order: Int = 0, callback: () -> Unit) {
        createVoiceUseCase.execute(
            token = getToken(),  //// INSERT!!!!!!!!!!!!
            communityId = communityId,
            name = name,
            directoryId = directoryId,
            order = order
        ) { data, error ->
            viewModelScope.launch(Dispatchers.Default) {
                errorMessage.value = error
                callback()
            }
        }
    }

    fun getToken(): String {
        return SessionManager.token ?: ""
    }

    fun createCommunity(
        name: String,
        desc: String,
        isPublic: Boolean,
        voiceRegion: String,
        voiceName: String,
        callback: () -> Unit
    ) {
        createCommunityUseCase.execute(
            token = getToken(),  //// INSERT!!!!!!!!!!!!
            name = name,
            desc = desc,
            isPublic = isPublic,
            voiceRegion = voiceRegion,
            voiceName = voiceName
        ) { community, error ->
            viewModelScope.launch(Dispatchers.Default) {
                if (community != null) {
                    myCommunities.value += community
                }
                errorMessage.value = error
                callback()
            }
        }
    }

    fun createDirectory(communityId: Long, name: String, directoryId: Long?, order: Int = 0, callback: () -> Unit) {
        createDirectoryUseCase.execute(
            token = getToken(),  //// INSERT!!!!!!!!!!!!
            communityId = communityId,
            name = name,
            directoryId = directoryId,
            order = order
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

    fun getMyProfileInfo(callback: () -> Unit) {
        getMyProfileInfo.execute(
            token = getToken(),  //// INSERT!!!!!!!!!!!!
        ) { data, error ->
            viewModelScope.launch(Dispatchers.Default) {
                if (data != null) {
                    profile.value = data
                    errorMessage.value = null
                } else {
                    errorMessage.value = error
                }

                callback()
            }
        }
    }

    fun sendMessage(chatId: Long, message: SendMessageRequest, callback: () -> Unit) {
        sendMessageUseCase.execute(
            token = getToken(),  //// INSERT!!!!!!!!!!!!
            chatId = chatId,
            messageDto = message,
        ) { error ->
            viewModelScope.launch(Dispatchers.Default) {
                errorMessage.value = error
                callback()
            }
        }
    }

    fun getAllMessages(chatId: Long, page: Long = 0, size: Long = 50, callback: () -> Unit) {
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

    fun getMembersState(callback: () -> Unit) {
        val userIds = members.value.filter { it != profile.value?.id }.distinct()
        for (id in userIds) {
            getUserStateUseCase.execute(
                token = getToken(),  //// INSERT!!!!!!!!!!!!
                userId = id
            ) { data, error ->
                viewModelScope.launch(Dispatchers.Default) {
                    if (data != null) {
                        membersState.value += data
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

    fun getUserNameMap(callback: () -> Unit) {
        val userIds = members.value.filter { it != profile.value?.id }.distinct()
        for (id in userIds) {
            getProfileInfoUseCase.execute(
                token = getToken(),  //// INSERT!!!!!!!!!!!!
                userId = id
            ) { data, error ->
                viewModelScope.launch(Dispatchers.Default) {
                    if (data != null) {
                        idUserNameMap.value = idUserNameMap.value.toMutableMap().apply {
                            this[id] = data
                        }
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

    fun getMembers(communityId: Long, callback: () -> Unit) {
        getMembersUseCase.execute(
            token = getToken(),  //// INSERT!!!!!!!!!!!!
            communityId = communityId
        ) { data, error ->
            viewModelScope.launch(Dispatchers.Default) {
                if (data != null) {
                    members.value = data
                    errorMessage.value = null
                } else {
                    errorMessage.value = error
                }
                errorMessage.value = error
                callback()
            }
        }
    }

    fun getAllDirectories(communityId: Long, callback: () -> Unit) {
        getAllDirectoriesUseCase.execute(
            token = getToken(),  //// INSERT!!!!!!!!!!!!
            communityId = communityId
        ) { data, error ->
            viewModelScope.launch(Dispatchers.Default) {
                if (data != null) {
                    directories.value = data
                    errorMessage.value = null
                } else {
                    errorMessage.value = error
                }
                errorMessage.value = error
                callback()
            }
        }
    }

    fun getAllVoices(communityId: Long, callback: () -> Unit) {
        getAllVoicesUseCase.execute(
            token = getToken(),  //// INSERT!!!!!!!!!!!!
            communityId = communityId
        ) { data, error ->
            viewModelScope.launch(Dispatchers.Default) {
                if (data != null) {
                    voices.value = data
                    errorMessage.value = null
                } else {
                    errorMessage.value = error
                }
                errorMessage.value = error
                callback()
            }
        }
    }

    fun deleteChat(chatId: Long, callback: () -> Unit) {
        deleteChatUseCase.execute(
            token = getToken(),  //// INSERT!!!!!!!!!!!!
            chatId = chatId
        ) { error ->
            viewModelScope.launch(Dispatchers.Default) {
                errorMessage.value = error
                callback()
            }
        }
    }

    fun updateChat(chatId: Long, name: String, directoryId: Long, order: Int = 0, callback: () -> Unit) {
        updateChatUseCase.execute(
            token = getToken(),  //// INSERT!!!!!!!!!!!!
            chatId = chatId,
            name = name,
            directoryId = directoryId,
            order = order
        ) { data, error ->
            viewModelScope.launch(Dispatchers.Default) {
                errorMessage.value = error
                callback()
            }
        }
    }

    fun deleteVoice(voiceId: Long, callback: () -> Unit) {
        deleteVoiceUseCase.execute(
            token = getToken(),  //// INSERT!!!!!!!!!!!!
            voiceId = voiceId
        ) { error ->
            viewModelScope.launch(Dispatchers.Default) {
                errorMessage.value = error
                callback()
            }
        }
    }

    fun updateVoice(voiceId: Long, name: String, directoryId: Long, order: Int = 0, callback: () -> Unit) {
        updateVoiceUseCase.execute(
            token = getToken(),  //// INSERT!!!!!!!!!!!!
            voiceId = voiceId,
            name = name,
            directoryId = directoryId,
            order = order
        ) { data, error ->
            viewModelScope.launch(Dispatchers.Default) {
                errorMessage.value = error
                callback()
            }
        }
    }

    fun deleteDirectory(directoryId: Long, callback: () -> Unit) {
        deleteDirectoryUseCase.execute(
            token = getToken(),  //// INSERT!!!!!!!!!!!!
            directoryId = directoryId
        ) { error ->
            viewModelScope.launch(Dispatchers.Default) {
                errorMessage.value = error
                callback()
            }
        }
    }

    fun updateDirectory(id: Long, name: String, directoryId: Long, order: Int = 0, callback: () -> Unit) {
        updateDirectoryUseCase.execute(
            token = getToken(),  //// INSERT!!!!!!!!!!!!
            id = id,
            name = name,
            directoryId = directoryId,
            order = order
        ) { error ->
            viewModelScope.launch(Dispatchers.Default) {
                errorMessage.value = error
                callback()
            }
        }
    }

}
