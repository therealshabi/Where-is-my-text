package technolifestyle.com.whereismytext

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.Mode
import com.otaliastudios.cameraview.PictureResult
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cameraView.setLifecycleOwner(this)
        cameraView.mode = Mode.PICTURE


        cameraView.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)
                result.toBitmap(900, 1800) { bitmap ->
                    imageView.setImageBitmap(bitmap)
                }
            }
        })

        captureButton.setOnClickListener {
            cameraView.takePicture()
            captureButton.isEnabled = false
            recaptureButton.isEnabled = true
            cameraView.visibility = View.GONE
            imageView.visibility = View.VISIBLE
        }

        recaptureButton.setOnClickListener {
            captureButton.isEnabled = true
            recaptureButton.isEnabled = false
            cameraView.visibility = View.VISIBLE
            imageView.visibility = View.GONE
        }
    }
}
