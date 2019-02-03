package technolifestyle.com.whereismytext

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AppCompatActivity
import com.otaliastudios.cameraview.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cameraView.setLifecycleOwner(this)
        cameraView.open()
        cameraView.mode = Mode.PICTURE
        cameraView.mapGesture(Gesture.TAP, GestureAction.FOCUS_WITH_MARKER) // Tap to focus!

        cameraView.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)
                result.toBitmap(900, 1800) { bitmap ->
                    if (bitmap != null) {
                        (application as BaseApplication).bitmap = bitmap
                        val intent = Intent(this@MainActivity, SearchActivity::class.java)
                        startActivity(intent)
                        cameraView.close()
                    } else {
                        Toast.makeText(
                            baseContext,
                            "Something went wrong, Try again later!",
                            LENGTH_LONG
                        ).show()
                    }
                }
            }
        })

        captureButton.setOnClickListener {
            cameraView.takePicture()
        }
    }

    override fun onBackPressed() {
        (application as BaseApplication).showBackButtonActionDialog(this, this@MainActivity)
    }
}
