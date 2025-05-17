package di

import AppConfig
import data.ChatApi
import data.DirectoryApi
import data.HomeApi
import data.MessageApi
import data.usecase.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import presentation.viewmodel.HomeViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import ru.kotlix.frame.gateway.client.*


val homeModule = module {
    // OkHttpClient
    single {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder().addInterceptor(interceptor).build()
    }

    // Retrofit instance
    single {
        Retrofit.Builder()
            //.baseUrl("http://84.54.59.98:30084")
            .baseUrl(AppConfig.BASE_URL)
            .client(get())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // API interface

    single<GatewayVoiceClient> {
        get<Retrofit>().create(GatewayVoiceClient::class.java)
    }

    single<GatewayServerClient> {
        get<Retrofit>().create(GatewayServerClient::class.java)
    }

    single<GatewayCommunityClient> {
        get<Retrofit>().create(GatewayCommunityClient::class.java)
    }

    single<GatewayProfileClient> {
        get<Retrofit>().create(GatewayProfileClient::class.java)
    }

    single<GatewayChatClient> {
        get<Retrofit>().create(GatewayChatClient::class.java)
    }

    single<GatewayMessageClient> {
        get<Retrofit>().create(GatewayMessageClient::class.java)
    }

    single<GatewayDirectoryClient> {
        get<Retrofit>().create(GatewayDirectoryClient::class.java)
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

    single {
        CreateCommunityUseCase(get())
    }

    single {
        FetchVoiceServersUseCase(get())
    }

    single {
        GetMyProfileInfo(get())
    }

    single {
        GetAllDirectoriesUseCase(get())
    }


    // ViewModel
    single {
        HomeViewModel(get(), get(), get(), get(), get(), get(), get(),
            get(), get(), get(), get(), get(), get(), get(), get(), get())
    }
}
