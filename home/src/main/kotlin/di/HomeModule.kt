package di

import AppConfig
import data.ChatApi
import data.DirectoryApi
import data.HomeApi
import data.MessageApi
import data.usecase.*
import okhttp3.OkHttpClient
import org.koin.dsl.module
import presentation.viewmodel.HomeViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import ru.kotlix.frame.gateway.api.GatewayChatApi
import ru.kotlix.frame.gateway.client.GatewayChatClient
import ru.kotlix.frame.gateway.client.GatewayVoiceClient

val homeModule = module {
    // OkHttpClient
    single {
        OkHttpClient.Builder().build()
    }

    // Retrofit instance
    single {
        Retrofit.Builder()
            .baseUrl("http://84.54.59.98:30084")
            //.baseUrl(AppConfig.BASE_URL)
            .client(get())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // API interface

    single<GatewayVoiceClient> {
        get<Retrofit>().create(GatewayVoiceClient::class.java)
    }

    single<HomeApi> {
        get<Retrofit>().create(HomeApi::class.java)
    }

    single<ChatApi> {
        get<Retrofit>().create(ChatApi::class.java)
    }

    single<DirectoryApi> {
        get<Retrofit>().create(DirectoryApi::class.java)
    }

    single<MessageApi> {
        get<Retrofit>().create(MessageApi::class.java)
    }

    // UseCase
    single {
        FetchCommunitiesUseCase(get())
    }

    single {
        FetchMyCommunitiesUseCase(get())
    }

    single {
        JoinCommunityUseCase(get())
    }

    single {
        LeaveCommunityUseCase(get())
    }

    single {
        CreateChatUseCase(get())
    }

    single {
        GetAllMessagesUseCase(get())
    }

    single {
        GetAllChatsUseCase(get())
    }

    single {
        CreateDirectoryUseCase(get())
    }

    single {
        SendMessageUseCase(get())
    }

    single {
        DeleteChatUseCase()
    }

    single {
        UpdateChatUseCase()
    }

    single {
        GetAllDirectoriesUseCase(get())
    }

    single {
        GetAllVoicesUseCase(get())
    }

    // ViewModel
    single {
        HomeViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())
    }
}
