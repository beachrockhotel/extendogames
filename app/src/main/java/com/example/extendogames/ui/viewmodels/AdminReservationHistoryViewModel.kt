package com.example.extendogames.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.extendogames.api.models.ReservationResponse
import com.example.extendogames.api.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminReservationHistoryViewModel : ViewModel() {

    private val _reservations = MutableLiveData<List<ReservationResponse>>()
    val reservations: LiveData<List<ReservationResponse>> get() = _reservations

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    init {
        loadReservations()
    }

    private fun loadReservations() {
        RetrofitClient.instance.getReservations().enqueue(object : Callback<List<ReservationResponse>> {
            override fun onResponse(call: Call<List<ReservationResponse>>, response: Response<List<ReservationResponse>>) {
                if (response.isSuccessful) {
                    _reservations.value = response.body() ?: emptyList()
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    _error.value = "Ошибка получения данных: $errorMessage"
                }
            }

            override fun onFailure(call: Call<List<ReservationResponse>>, t: Throwable) {
                _error.value = "Ошибка сети: ${t.message}"
            }
        })
    }
}
