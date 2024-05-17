package com.dicoding.asclepius.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.databinding.ActivityNewsBinding
import com.dicoding.asclepius.response.ResponseCancerNews
import com.dicoding.asclepius.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsBinding
    private lateinit var adapter: ResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.news.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@NewsActivity)
        }

        adapter = ResultAdapter()
        binding.news.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        getDataFromApi()
    }

    private fun getDataFromApi() {
        showLoading(true)
        val apiService = ApiConfig.apiService

        apiService.getNews("Cancer").enqueue(object : Callback<ResponseCancerNews> {
            override fun onResponse(
                call: Call<ResponseCancerNews>,
                response: Response<ResponseCancerNews>
            ) {
                if (response.isSuccessful) {
                    val newsResponse = response.body()

                    val articles = newsResponse?.articles ?: emptyList()
                    adapter.submitArticlesList(articles)
                    showLoading(false)
                } else {

                }
            }

            override fun onFailure(call: Call<ResponseCancerNews>, t: Throwable) {

            }
        })
    }
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}
