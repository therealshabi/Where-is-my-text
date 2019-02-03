package technolifestyle.com.whereismytext

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
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
    private var lineBag: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        bitmap = (application as BaseApplication).bitmap

        imageView.setImageBitmap(bitmap)

        processBitmap(bitmap)

        recaptureButton.setOnClickListener {
            recapture()
        }

        searchText.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(textView: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val text = textView?.text
                    if (!text.isNullOrEmpty()) {
                        searchText(text.toString())
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun recapture() {
        startActivity(Intent(this@SearchActivity, MainActivity::class.java))
        finish()
    }

    private fun searchText(text: String) {
        for (element in wordBag) {
            if (element.contains(text, true)) {
                Toast.makeText(baseContext, "Text Found element: $text", Toast.LENGTH_LONG).show()
                searchText.text.clear()
                return
            }
        }

        for (element in lineBag) {
            if (element.contains(text, true)) {
                Toast.makeText(baseContext, "Text Found element: $text", Toast.LENGTH_LONG).show()
                searchText.text.clear()
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
                lineBag.add(line.text)
                for (element in line.elements) {
                    wordBag.add(element.text)
                }
            }
        }
        if (lineBag.isEmpty() && wordBag.isEmpty()) {
            showNoContentAlert()
        }
    }

    private fun showNoContentAlert() {
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog)
        } else {
            AlertDialog.Builder(this)
        }
        builder.setMessage("Oops! No Text Found")
            .setPositiveButton("Recapture") { dialog, _ ->
                recapture()
                dialog.dismiss()
            }
        runOnUiThread {
            builder.show()
        }
    }
}
