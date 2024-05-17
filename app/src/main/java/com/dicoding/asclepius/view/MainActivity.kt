package com.dicoding.asclepius.view

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.ActivityResultLauncher
import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.yalantis.ucrop.UCrop
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var isImageCropped = false

    private lateinit var mainviewmodel: MainViewModel
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private var currentImageUri: Uri? = null


    private lateinit var launcherGallery: ActivityResultLauncher<PickVisualMediaRequest>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainviewmodel = ViewModelProvider(this).get(MainViewModel::class.java)

        binding.galleryButton.setOnClickListener { startGallery() }




        imageClassifierHelper = ImageClassifierHelper(
            threshold = 0.3f,
            maxResults = 1,
            modelName = "cancer_classification(1).tflite",
            context = this,
            contentResolver = contentResolver,
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    showToast(error)
                }

                override fun onResults(
                    results: List<Classifications>?,
                    inferenceTime: Long
                ) {
                    results?.let { classifications ->

                    } ?: run {
                        showToast("Gagal mendapatkan hasil klasifikasi")
                    }
                }
            }
        )


        launcherGallery = registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri: Uri? ->
            if (uri != null) {
                currentImageUri = uri
                showImage()
                analyzeImage()
            } else {
                Log.d("Photo Picker", "No media selected")
            }
        }
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.info -> {
                    val intent = Intent(this, NewsActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.riwayat -> {
                    val intent = Intent(this, HistoryActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }

    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }


    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
            val reducedUri = reducedFile(it)
            reducedUri?.let { uri ->
                currentImageUri = uri
            }
        }
        binding.analyzeButton.setOnClickListener {
            analyzeImage()
            moveToResult()

        }
        binding.cropButton.setOnClickListener {
            currentImageUri?.let { uri ->
                cropImage(uri)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            val resultUri = UCrop.getOutput(data!!)
            resultUri?.let {
                currentImageUri = it
                showImage()
                analyzeImage()
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            cropError?.message?.let {
                showToast(it)
            }
        }
    }

    private fun cropImage(uri: Uri) {
        if (!isImageCropped) {
            val options = UCrop.Options().apply {
                val options = UCrop.Options().apply {
                    setToolbarColor(ContextCompat.getColor(this@MainActivity, R.color.black))
                    setStatusBarColor(ContextCompat.getColor(this@MainActivity, R.color.black))
                    setToolbarWidgetColor(ContextCompat.getColor(this@MainActivity, R.color.white))
                    setCropFrameColor(ContextCompat.getColor(this@MainActivity, R.color.blue))
                    setCropGridColor(ContextCompat.getColor(this@MainActivity, R.color.blue))
                    setCompressionQuality(100)
                    setShowCropGrid(true)
                    setShowCropFrame(true)
                }
            }

            val destinationUri = Uri.fromFile(File(cacheDir, "cropped_image.jpg"))

            UCrop.of(uri, destinationUri)
                .withOptions(options)
                .start(this)

            isImageCropped = true
        } else {
            showToast("Anda hanya dapat memotong gambar sekali")
        }

    }


    private fun reducedFile(uri: Uri): Uri? {
        val inputStream = contentResolver.openInputStream(uri)
        inputStream?.let {
            val bitmap = BitmapFactory.decodeStream(it)

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "IMG_$timeStamp.jpg"

            val file = File(cacheDir, fileName)

            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()

            return Uri.fromFile(file)
        }
        return null
    }


    private fun analyzeImage() {
        currentImageUri?.let { imageUri ->
            imageClassifierHelper.classifyImage(imageUri)

        }
    }


    private fun moveToResult() {
        imageClassifierHelper.getClassifications()?.let { classifications ->
            if (classifications.isNotEmpty()) {
                val topPrediction = classifications[0].categories[0]


                mainviewmodel.insert(
                    currentImageUri.toString(),
                    topPrediction.label,
                    topPrediction.score
                )
                val intent = Intent(this, ResultActivity::class.java).apply {
                    putExtra("prediction", topPrediction.label)
                    putExtra("confidence_score", topPrediction.score)
                    putExtra("image_uri", currentImageUri)
                }
                startActivity(intent)

            } else {
                showToast("Gagal mendapatkan hasil klasifikasi")

            }
        } ?: showToast("Gagal mendapatkan hasil klasifikasi")
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
