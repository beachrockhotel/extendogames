package com.example.extendogames.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.extendogames.api.models.NewsItem
import com.example.extendogames.api.responses.NewsResponse
import com.example.extendogames.api.services.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NewsViewModel : ViewModel() {
    private val _newsItems = MutableLiveData<List<NewsItem>>()
    val newsItems: LiveData<List<NewsItem>> = _newsItems

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val newsService: ApiService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        newsService = retrofit.create(ApiService::class.java)
        loadNews()
    }

    private fun loadNews() {
        newsService.getNews().enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                if (response.isSuccessful) {
                    _newsItems.value = response.body()?.news ?: emptyList()
                } else {
                    _errorMessage.value = "Ошибка: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                _errorMessage.value = "Ошибка загрузки новостей: ${t.message}"
            }
        })
    }
}
