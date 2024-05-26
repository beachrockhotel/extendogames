package com.example.extendogames.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.extendogames.api.models.Order
import com.example.extendogames.api.services.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderHistoryViewModel : ViewModel() {
    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>> get() = _orders

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun loadOrders() {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: return
        RetrofitClient.instance.getUserOrders(userEmail).enqueue(object : Callback<List<Order>> {
            override fun onResponse(call: Call<List<Order>>, response: Response<List<Order>>) {
                if (response.isSuccessful) {
                    _orders.value = response.body() ?: emptyList()
                } else {
                    _errorMessage.value = "Ошибка загрузки данных: ${response.errorBody()?.string()}"
                }
            }

            override fun onFailure(call: Call<List<Order>>, t: Throwable) {
                _errorMessage.value = "Ошибка подключения: ${t.message}"
            }
        })
    }
}