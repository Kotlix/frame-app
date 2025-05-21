package di

import com.google.gson.GsonBuilder
import data.usecases.JoinVoiceChatUseCase
import data.usecases.LeaveVoiceUseCase
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import presentation.viewmodel.VoiceViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import ru.kotlix.frame.gateway.client.GatewayVoiceClient
import utils.LocalDateTimeAdapter
import java.time.LocalDateTime

val voiceModule = module {
    // OkHttpClient
    single {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder().addInterceptor(interceptor).build()
    }

    // Retrofit instance
    single {
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
            .create()

        Retrofit.Builder()
            //.baseUrl("http://84.54.59.98:30084")
            .baseUrl(AppConfig.BASE_URL)
            .client(get())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // API interface

    single<GatewayVoiceClient> {
        get<Retrofit>().create(GatewayVoiceClient::class.java)
    }

    single {
        JoinVoiceChatUseCase(get())
    }

    single {
        LeaveVoiceUseCase(get())
    }

    single {
        VoiceViewModel(get(), get())
    }
}