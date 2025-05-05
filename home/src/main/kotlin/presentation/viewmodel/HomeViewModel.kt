package presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import data.usecase.FetchCommunitiesUseCase
import data.usecase.FetchMyCommunitiesUseCase
import data.usecase.JoinCommunityUseCase
import data.usecase.LeaveCommunityUseCase
import dto.CommunityEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import session.SessionManager

class HomeViewModel(
    private val fetchCommunitiesUseCase: FetchCommunitiesUseCase,
    private val fetchMyCommunitiesUseCase: FetchMyCommunitiesUseCase,
    private val joinCommunityUseCase: JoinCommunityUseCase,
    private val leaveCommunityUseCase: LeaveCommunityUseCase
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

    fun getToken(): String {
        return SessionManager.token ?: ""
    }

}
