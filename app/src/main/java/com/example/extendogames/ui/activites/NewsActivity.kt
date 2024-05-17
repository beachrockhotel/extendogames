package com.example.extendogames.ui.activites

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.extendogames.R
import com.example.extendogames.api.models.NewsItem
import com.example.extendogames.ui.adapters.NewsAdapter
import com.example.extendogames.ui.viewmodels.NewsViewModel
import java.util.ArrayList

class NewsActivity : AppCompatActivity() {
    private val viewModel: NewsViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NewsAdapter
    private var newsList: ArrayList<NewsItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        recyclerView = findViewById(R.id.newsRecyclerView)
        adapter = NewsAdapter(newsList)
        recyclerView.adapter = adapter

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.newsItems.observe(this, Observer { newsItems ->
            newsList.clear()
            newsList.addAll(newsItems)
            adapter.notifyDataSetChanged()
        })

        viewModel.errorMessage.observe(this, Observer { errorMessage ->
            Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
        })
    }
}
