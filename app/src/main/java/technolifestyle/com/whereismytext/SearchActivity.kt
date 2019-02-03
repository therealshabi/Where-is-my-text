package technolifestyle.com.whereismytext

import android.app.AlertDialog
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import kotlinx.android.synthetic.main.activity_search.*
import timber.log.Timber


class SearchActivity : AppCompatActivity() {

    private lateinit var bitmap: Bitmap
    private var wordBag: HashMap<String, ArrayList<Rect>> = HashMap()
    private var lineBag: HashMap<String, ArrayList<Rect>> = HashMap()
    private lateinit var mutableBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        bitmap = (application as BaseApplication).bitmap

        imageView.setOnTouchListener(ImageMatrixTouchHandler(baseContext))

        imageView.setImageBitmap(bitmap)

        processBitmap(bitmap)

        recaptureButton.setOnClickListener {
            recapture()
        }

        resetBitmap()

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

    private fun resetBitmap() {
        mutableBitmap = bitmap.copy(bitmap.config, true)
    }

    private fun searchText(text: String) {
        resetBitmap()
        val listBoundary: ArrayList<Rect> = ArrayList()
        for (element in wordBag) {
            if (element.key.contains(text, true)) {
                listBoundary.addAll(element.value)
            }
        }

        if (listBoundary.isEmpty()) {
            for (element in lineBag) {
                if (element.key.contains(text, true)) {
                    listBoundary.addAll(element.value)
                }
            }
        }

        if (listBoundary.isNotEmpty()) {
            Toast.makeText(baseContext, getString(R.string.text_found_text), Toast.LENGTH_LONG).show()
            setImageBoundary(listBoundary)
            searchText.text.clear()
            return
        }

        Toast.makeText(baseContext, getString(R.string.no_text_found_text), Toast.LENGTH_LONG).show()
    }

    private fun setImageBoundary(boundaries: ArrayList<Rect>) {
        // draw all bounding boxes on bitmap
        with(Canvas(mutableBitmap)) {
            val paint = Paint()
            paint.style = Paint.Style.STROKE
            paint.color = Color.RED
            boundaries.forEach { boundary ->
                drawRect(boundary, paint)
            }
        }
        imageView.setImageBitmap(mutableBitmap)
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
                if (line.boundingBox != null) {
                    if (lineBag[line.text] == null) {
                        lineBag[line.text] = ArrayList()
                    }
                    lineBag[line.text]?.add(line.boundingBox!!)
                }
                for (element in line.elements) {
                    if (wordBag[element.text] == null) {
                        wordBag[element.text] = ArrayList()
                    }
                    wordBag[element.text]?.add(element.boundingBox!!)
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
        builder.setMessage(getString(R.string.no_text_found_text))
            .setPositiveButton(getString(R.string.recapture)) { dialog, _ ->
                recapture()
                dialog.dismiss()
            }
        runOnUiThread {
            builder.show()
        }
    }
}
