package presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import data.usecase.FetchCommunitiesUseCase
import data.usecase.FetchMyCommunitiesUseCase
import data.usecase.JoinCommunityUseCase
import data.usecase.LeaveCommunityUseCase
import dto.CommunityEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(
    private val fetchCommunitiesUseCase: FetchCommunitiesUseCase,
    private val fetchMyCommunitiesUseCase: FetchMyCommunitiesUseCase,
    private val joinCommunityUseCase: JoinCommunityUseCase,
    private val leaveCommunityUseCase: LeaveCommunityUseCase
) {

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
            token = "",  //// INSERT!!!!!!!!!!!!
            name = search,
            pageOffset = pageOffset,
            pageCount = pageCount
        ) { data, error ->
            kotlinx.coroutines.GlobalScope.launch(Dispatchers.Main) {
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
            token = "",  //// INSERT!!!!!!!!!!!!
        ) { data, error ->
            kotlinx.coroutines.GlobalScope.launch(Dispatchers.Main) {
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
            token = "",  //// INSERT!!!!!!!!!!!!
            communityId = communityId
        ) { error ->
            kotlinx.coroutines.GlobalScope.launch(Dispatchers.Main) {
                errorMessage.value = error
                callback()
            }
        }
    }

    fun leaveCommunity(communityId: Long, callback: () -> Unit) {
        leaveCommunityUseCase.execute(
            token = "",  //// INSERT!!!!!!!!!!!!
            communityId = communityId
        ) { error ->
            kotlinx.coroutines.GlobalScope.launch(Dispatchers.Main) {
                errorMessage.value = error
                callback()
            }
        }
    }


}
