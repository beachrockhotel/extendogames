package com.example.extendogames.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.extendogames.api.models.ReservationResponse
import com.example.extendogames.api.services.ApiService
import com.example.extendogames.api.services.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReservationHistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val _reservations = MutableLiveData<List<ReservationResponse>>()
    val reservations: LiveData<List<ReservationResponse>> get() = _reservations

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    private val apiService: ApiService = RetrofitClient.instance

    init {
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
