package technolifestyle.com.whereismytext

import android.app.Application
import android.graphics.Bitmap
import timber.log.Timber

class BaseApplication : Application() {

    public lateinit var bitmap: Bitmap

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
