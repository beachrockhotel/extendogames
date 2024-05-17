package com.example.extendogames.ui.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.extendogames.api.models.Review
import com.example.extendogames.api.services.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReviewViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = RetrofitClient.instance
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val _reviews = MutableLiveData<List<Review>>()
    val reviews: LiveData<List<Review>> get() = _reviews

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    init {
        loadReviews()
    }

    private fun loadReviews() {
        apiService.getReviews().enqueue(object : Callback<List<Review>> {
            override fun onResponse(call: Call<List<Review>>, response: Response<List<Review>>) {
                if (response.isSuccessful) {
                    _reviews.value = response.body()
                } else {
                    _message.value = "Ошибка загрузки отзывов: ${response.errorBody()?.string()}"
                }
            }

            override fun onFailure(call: Call<List<Review>>, t: Throwable) {
                _message.value = "Ошибка подключения: ${t.localizedMessage}"
            }
        })
    }

    fun postReview(review: Review) {
        apiService.postReview(review).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    _message.value = "Отзыв успешно добавлен"
                    loadReviews() // Обновить список отзывов после успешного добавления
                } else {
                    _message.value = "Ошибка при добавлении отзыва: ${response.errorBody()?.string()}"
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                _message.value = "Ошибка подключения: ${t.localizedMessage}"
            }
        })
    }

    fun getCurrentUserEmail(): String {
        return auth.currentUser?.email ?: "email@notavailable.com"
    }
}
