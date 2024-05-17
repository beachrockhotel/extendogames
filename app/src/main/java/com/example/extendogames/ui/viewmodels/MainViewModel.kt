package com.example.extendogames.ui.activites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.extendogames.api.models.ReservationRequest
import com.example.extendogames.api.responses.AvailabilityResponse
import com.example.extendogames.api.services.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel(private val apiService: ApiService) : ViewModel() {

    fun checkStationAvailability(placeNumber: String, callback: (String, Boolean) -> Unit) {
        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val request = ReservationRequest(
            placeNumber = placeNumber,
            date = todayDate,
            time = currentTime,
            duration = 1,
            userEmail = "qwe@mail.ru",
            userName = "Dmitriy"
        )
        apiService.checkAvailability(request).enqueue(object : retrofit2.Callback<AvailabilityResponse> {
            override fun onResponse(call: retrofit2.Call<AvailabilityResponse>, response: retrofit2.Response<AvailabilityResponse>) {
                val isAvailable = response.isSuccessful && response.body()?.available ?: false
                viewModelScope.launch(Dispatchers.Main) {
                    callback(placeNumber, isAvailable)
                }
            }

            override fun onFailure(call: retrofit2.Call<AvailabilityResponse>, t: Throwable) {
                viewModelScope.launch(Dispatchers.Main) {
                    callback(placeNumber, false)
                }
            }
        })
    }
}
