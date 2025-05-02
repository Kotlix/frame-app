package di

import AppConfig
import data.api.AuthApi
import data.usecase.LoginUseCase
import data.usecase.RegisterUseCase
import okhttp3.OkHttpClient
import org.koin.dsl.module
import presentation.viewmodel.AuthViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val authModule = module {

    single {
        OkHttpClient.Builder()
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(AppConfig.BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<AuthApi> {
        get<Retrofit>().create(AuthApi::class.java)
    }

    single {
        LoginUseCase(get())
    }

    single {
        RegisterUseCase(get())
    }

    single {
        AuthViewModel(get(), get())
    }

}
