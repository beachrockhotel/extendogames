package com.example.extendogames.ui.viewmodels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.extendogames.api.models.ReservationResponse
import com.example.extendogames.api.services.ApiService
import com.google.firebase.auth.FirebaseAuth
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ReservationHistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val _reservations = MutableLiveData<List<ReservationResponse>>()
    val reservations: LiveData<List<ReservationResponse>> get() = _reservations

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    private val apiService: ApiService

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().addInterceptor(logging).build())
            .build()

        apiService = retrofit.create(ApiService::class.java)
        loadReservations()
    }

    private fun loadReservations() {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: return
        apiService.getUserReservations(userEmail).enqueue(object : Callback<List<ReservationResponse>> {
            override fun onResponse(call: Call<List<ReservationResponse>>, response: Response<List<ReservationResponse>>) {
                if (response.isSuccessful) {
                    _reservations.value = response.body()
                } else {
                    _message.value = "Ошибка загрузки данных: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<List<ReservationResponse>>, t: Throwable) {
                _message.value = "Ошибка подключения: ${t.message}"
            }
        })
    }
}