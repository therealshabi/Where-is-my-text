package technolifestyle.com.whereismytext

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import kotlinx.android.synthetic.main.activity_search.*
import timber.log.Timber


class SearchActivity : AppCompatActivity() {

    private lateinit var bitmap: Bitmap
    private var wordBag: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        bitmap = (application as BaseApplication).bitmap

        imageView.setImageBitmap(bitmap)

        processBitmap(bitmap)

        recaptureButton.setOnClickListener {
            startActivity(Intent(this@SearchActivity, MainActivity::class.java))
        }

        searchText.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(textView: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val text = textView?.text
                    textView?.text = ""
                    if (!text.isNullOrEmpty()) {
                        searchText(text.toString())
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun searchText(text: String) {
        for (element in wordBag) {
            if (element.contains(text, true)) {
                Toast.makeText(baseContext, "Text Found element: $text", Toast.LENGTH_LONG).show()
                return
            }
        }
        Toast.makeText(baseContext, "Not Found", Toast.LENGTH_LONG).show()
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
                for (element in line.elements) {
                    wordBag.add(element.text)
                }
            }
        }
    }
}
