package di

import AppConfig
import com.google.gson.GsonBuilder
import data.api.AuthApi
import data.usecase.LoginUseCase
import data.usecase.RegisterUseCase
import data.usecase.VerifyCodeUseCase
import okhttp3.OkHttpClient
import org.koin.dsl.module
import presentation.viewmodel.AuthViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

val authModule = module {

//    single {
//        OkHttpClient.Builder()
//            .build()
//    }
//
//    single {
//        Retrofit.Builder()
//            //.baseUrl("http://localhost:8080")
//            .baseUrl(AppConfig.BASE_URL)
//            .client(get())
//            .addConverterFactory(ScalarsConverterFactory.create())
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }

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
        VerifyCodeUseCase(get())
    }


    single {
        AuthViewModel(get(), get(), get())
    }

}
