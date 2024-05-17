package com.dicoding.asclepius.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.Room.ResultEntity
import com.dicoding.asclepius.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var adapter: ResultAdapter
    private lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showLoading(true)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        binding.history.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@HistoryActivity)
        }

        adapter = ResultAdapter()

        binding.history.adapter = adapter



        viewModel.getdatahistory()?.observe(this) { users ->
            val items = arrayListOf<ResultEntity>()
            users.map {
                val formattedConfidenceScore = it.confidenceScore?.times(100)?.toInt() ?: 0
                val item = ResultEntity(
                    imageUri = it.imageUri,
                    prediction = it.prediction,
                    confidenceScore = formattedConfidenceScore.toFloat()
                )
                items.add(item)
            }
            adapter.submitList(items)
            showLoading(false)
        }

    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}