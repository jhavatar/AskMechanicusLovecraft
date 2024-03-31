package io.chthonic.mechanicuslovecraft

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.chthonic.mechanicuslovecraft.domain.localconfig.InitializeLocalConfigUseCase
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var initializeLocalConfigUseCaseHolder: dagger.Lazy<InitializeLocalConfigUseCase>

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        runBlocking {
            initializeLocalConfigUseCaseHolder.get()
                .execute(openAiApiKey = BuildConfig.OPENAI_API_KEY, openAiOrganization = BuildConfig.OPENAI_ORGANIZATION)
        }
    }
}