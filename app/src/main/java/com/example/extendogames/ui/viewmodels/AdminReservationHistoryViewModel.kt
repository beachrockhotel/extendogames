package com.example.extendogames.ui.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.extendogames.api.models.ReservationResponse
import com.example.extendogames.api.services.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminReservationHistoryViewModel(application: Application) : AndroidViewModel(application) {

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

    fun clearReservationHistory() {
        viewModelScope.launch {
            RetrofitClient.instance.clearReservationHistory().enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        loadReservations()
                        Toast.makeText(getApplication(), "История успешно очищена", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(getApplication(), "Не удалось очистить историю", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(getApplication(), "Ошибка: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
