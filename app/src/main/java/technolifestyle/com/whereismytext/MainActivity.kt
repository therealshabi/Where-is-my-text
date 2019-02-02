package technolifestyle.com.whereismytext

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.Mode
import com.otaliastudios.cameraview.PictureResult
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

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
                    if (bitmap != null) {
                        processBitmap(bitmap)
                    }
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

    private fun processBitmap(bitmap: Bitmap) {
        val image = FirebaseVisionImage.fromBitmap(bitmap)
        val detector = FirebaseVision.getInstance()
            .onDeviceTextRecognizer

        detector.processImage(image)
            .addOnSuccessListener { firebaseVisionText ->
                Timber.d("Text: %s", firebaseVisionText.text)
                processText(firebaseVisionText)
            }
            .addOnFailureListener {
                Timber.e(it)
            }
    }

    private fun processText(firebaseVisionText: FirebaseVisionText) {
        for (block in firebaseVisionText.textBlocks) {
            for (line in block.lines) {
                Timber.d("Line: %s", line.text)
            }
        }
    }
}
