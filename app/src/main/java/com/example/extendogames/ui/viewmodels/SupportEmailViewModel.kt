package com.example.extendogames.ui.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.extendogames.api.requests.SupportRequest
import com.example.extendogames.api.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SupportEmailViewModel(application: Application) : AndroidViewModel(application) {

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    fun sendSupportRequest(email: String, question: String, name: String) {
        val request = SupportRequest(email, question, name)
        RetrofitClient.instance.sendSupportRequest(request)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        _message.value = "Запрос отправлен"
                    } else {
                        _message.value = "Ошибка отправки запроса: ${response.message()}"
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    _message.value = "Ошибка сети: ${t.message}"
                }
            })
    }
}
