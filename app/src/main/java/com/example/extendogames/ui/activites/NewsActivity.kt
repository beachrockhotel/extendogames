package com.example.extendogames.ui.activites

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.extendogames.R
import com.example.extendogames.api.services.ApiService
import com.example.extendogames.api.models.NewsItem
import com.example.extendogames.api.responses.NewsResponse
import com.example.extendogames.ui.adapters.NewsAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.ArrayList

class NewsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NewsAdapter
    private var newsList: ArrayList<NewsItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        recyclerView = findViewById(R.id.newsRecyclerView)
        adapter = NewsAdapter(newsList)
        recyclerView.adapter = adapter

        loadNews()
    }

    private fun loadNews() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val newsService = retrofit.create(ApiService::class.java)
        newsService.getNews().enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                if (response.isSuccessful) {
                    val newsItems = response.body()?.news ?: emptyList()
                    newsList.clear()
                    newsList.addAll(newsItems)
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(applicationContext, "Ошибка: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Ошибка загрузки новостей: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
