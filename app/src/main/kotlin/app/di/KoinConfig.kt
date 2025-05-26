package app.di

import di.authModule
import di.homeModule
import di.voiceModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(appModule, authModule, homeModule, voiceModule)
    }
}
