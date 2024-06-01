package com.example.extendogames.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.extendogames.api.requests.SupportRequest
import com.example.extendogames.api.services.RetrofitClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminSupportEmailViewModel : ViewModel() {

    private val _supportRequests = MutableLiveData<List<SupportRequest>>()
    val supportRequests: LiveData<List<SupportRequest>> get() = _supportRequests

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _clearContactInfoSuccess = MutableLiveData<String>()
    val clearContactInfoSuccess: LiveData<String> get() = _clearContactInfoSuccess

    init {
        loadSupportRequests()
    }

    fun loadSupportRequests() {
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

    fun clearContactInfo() {
        RetrofitClient.instance.clearSupportRequests().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    _clearContactInfoSuccess.value = "Вопросы успешно очищены"
                    loadSupportRequests()
                } else {
                    _error.value = "Ошибка при очистке: ${response.code()} ${response.message()}"
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                _error.value = "Ошибка сети: ${t.message}"
            }
        })
    }
}
