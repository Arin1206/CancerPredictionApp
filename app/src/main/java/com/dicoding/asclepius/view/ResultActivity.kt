package com.dicoding.asclepius.view

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.asclepius.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val imageUri = intent.getParcelableExtra<Uri>("image_uri")
        val prediction = intent.getStringExtra("prediction")
        val confidenceScore = intent.getFloatExtra("confidence_score", 0f)


        binding.resultImage.setImageURI(imageUri)
        val formattedConfidenceScore = String.format("%.2f", confidenceScore * 100)
        binding.resultText.text = "$prediction $formattedConfidenceScore%"


    }
}
