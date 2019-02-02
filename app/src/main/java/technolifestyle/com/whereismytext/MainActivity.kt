package technolifestyle.com.whereismytext

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    companion object {
        const val CAMERA_CAPTURE_CODE: Int = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        capture.setOnClickListener {
            val intent = Intent("android.media.action.IMAGE_CAPTURE")
            startActivityForResult(intent, CAMERA_CAPTURE_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CAMERA_CAPTURE_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                val photo = data?.extras?.get("data") as Bitmap

                imageView.setImageBitmap(photo)
                Timber.d("Data: %s", data)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
