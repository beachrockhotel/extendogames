package com.example.extendogames.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.extendogames.api.responses.PhoneNumberResponse
import com.example.extendogames.api.services.RetrofitClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminSupportPhoneViewModel : ViewModel() {

    private val _phoneNumbers = MutableLiveData<List<PhoneNumberResponse>>()
    val phoneNumbers: LiveData<List<PhoneNumberResponse>> get() = _phoneNumbers

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _clearSuccess = MutableLiveData<String>()
    val clearSuccess: LiveData<String> get() = _clearSuccess

    init {
        loadPhoneNumbers()
    }

    fun loadPhoneNumbers() {
        RetrofitClient.instance.getSupportPhoneNumbers().enqueue(object : Callback<List<PhoneNumberResponse>> {
            override fun onResponse(call: Call<List<PhoneNumberResponse>>, response: Response<List<PhoneNumberResponse>>) {
                if (response.isSuccessful) {
                    _phoneNumbers.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Ошибка получения данных: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<List<PhoneNumberResponse>>, t: Throwable) {
                _error.value = "Ошибка сети: ${t.message}"
            }
        })
    }

    fun clearPhoneNumbers() {
        RetrofitClient.instance.clearSupportPhones().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    _clearSuccess.value = "Номера телефонов успешно очищены"
                    loadPhoneNumbers()  // Reload the list after clearing
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
