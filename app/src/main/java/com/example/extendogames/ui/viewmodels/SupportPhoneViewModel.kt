package com.example.extendogames.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.extendogames.api.requests.PhoneNumberRequest
import com.example.extendogames.api.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SupportPhoneViewModel(application: Application) : AndroidViewModel(application) {

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    fun sendPhoneNumber(phoneNumber: String) {
        RetrofitClient.instance.sendPhoneNumber(PhoneNumberRequest(phoneNumber))
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        _message.value = "Номер телефона успешно отправлен"
                    } else {
                        _message.value = "Не удалось отправить номер телефона"
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    _message.value = "Ошибка: ${t.message}"
                }
            })
    }
}
