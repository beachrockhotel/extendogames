package com.example.extendogames.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.extendogames.api.responses.FoodOrderStatisticsResponse
import com.example.extendogames.api.services.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminFoodOrderStatisticsViewModel : ViewModel() {

    private val _statistics = MutableLiveData<Map<String, FoodOrderStatisticsResponse>?>()
    val statistics: LiveData<Map<String, FoodOrderStatisticsResponse>?> get() = _statistics

    fun fetchStatistics(fromDate: String, toDate: String) {
        viewModelScope.launch {
            RetrofitClient.instance.getFoodOrderStatistics(fromDate, toDate)
                .enqueue(object : Callback<Map<String, FoodOrderStatisticsResponse>> {
                    override fun onResponse(
                        call: Call<Map<String, FoodOrderStatisticsResponse>>,
                        response: Response<Map<String, FoodOrderStatisticsResponse>>
                    ) {
                        if (response.isSuccessful) {
                            _statistics.value = response.body()
                        } else {
                            _statistics.value = null
                        }
                    }

                    override fun onFailure(
                        call: Call<Map<String, FoodOrderStatisticsResponse>>,
                        t: Throwable
                    ) {
                        _statistics.value = null
                    }
                })
        }
    }
}
