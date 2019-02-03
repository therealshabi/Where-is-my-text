package technolifestyle.com.whereismytext

import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import timber.log.Timber

class BaseApplication : Application() {

    lateinit var bitmap: Bitmap

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    fun showBackButtonActionDialog(context: Context, activity: Activity) {
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Dialog)
        } else {
            AlertDialog.Builder(context)
        }
        builder.setMessage(getString(R.string.back_button_alert_message))
            .setPositiveButton(android.R.string.yes) { dialog, _ ->
                activity.finish()
                dialog.dismiss()
            }.setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
        activity.runOnUiThread {
            builder.show()
        }
    }
}
