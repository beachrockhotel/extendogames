package com.example.extendogames.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.extendogames.api.responses.ReservationStatisticsResponse
import com.example.extendogames.api.services.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminReservationStatisticsViewModel : ViewModel() {

    private val _statistics = MutableLiveData<Map<String, ReservationStatisticsResponse>?>()
    val statistics: LiveData<Map<String, ReservationStatisticsResponse>?> get() = _statistics

    fun fetchStatistics(fromDate: String, toDate: String) {
        viewModelScope.launch {
            RetrofitClient.instance.getReservationStatistics(fromDate, toDate)
                .enqueue(object : Callback<Map<String, ReservationStatisticsResponse>> {
                    override fun onResponse(
                        call: Call<Map<String, ReservationStatisticsResponse>>,
                        response: Response<Map<String, ReservationStatisticsResponse>>
                    ) {
                        if (response.isSuccessful) {
                            _statistics.value = response.body()
                        } else {
                            _statistics.value = null
                        }
                    }

                    override fun onFailure(
                        call: Call<Map<String, ReservationStatisticsResponse>>,
                        t: Throwable
                    ) {
                        _statistics.value = null
                    }
                })
        }
    }
}