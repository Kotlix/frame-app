package di

import AppConfig
import data.HomeApi
import data.usecase.FetchCommunitiesUseCase
import data.usecase.FetchMyCommunitiesUseCase
import data.usecase.JoinCommunityUseCase
import data.usecase.LeaveCommunityUseCase
import okhttp3.OkHttpClient
import org.koin.dsl.module
import presentation.viewmodel.HomeViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val homeModule = module {
    // OkHttpClient
    single {
        OkHttpClient.Builder().build()
    }

    // Retrofit instance
    single {
        Retrofit.Builder()
            //.baseUrl("http://localhost:8080/")
            .baseUrl(AppConfig.BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // API interface
    single<HomeApi> {
        get<Retrofit>().create(HomeApi::class.java)
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

    // ViewModel
    single {
        HomeViewModel(get(), get(), get(), get())
    }
}
