package com.example.extendogames.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.extendogames.api.requests.SupportRequest
import com.example.extendogames.api.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminSupportListViewModel : ViewModel() {

    private val _supportRequests = MutableLiveData<List<SupportRequest>>()
    val supportRequests: LiveData<List<SupportRequest>> get() = _supportRequests

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    init {
        loadSupportRequests()
    }

    private fun loadSupportRequests() {
        RetrofitClient.instance.getSupportRequests().enqueue(object : Callback<List<SupportRequest>> {
            override fun onResponse(call: Call<List<SupportRequest>>, response: Response<List<SupportRequest>>) {
                if (response.isSuccessful && response.body() != null) {
                    _supportRequests.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Ошибка получения данных: ${response.code()} ${response.message()}"
                }
            }

            override fun onFailure(call: Call<List<SupportRequest>>, t: Throwable) {
                _error.value = "Ошибка сети: ${t.message}"
            }
        })
    }
}
