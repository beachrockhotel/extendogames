package com.example.extendogames.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.extendogames.api.models.Order
import com.example.extendogames.api.responses.OrdersResponse
import com.example.extendogames.api.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminOrderHistoryViewModel : ViewModel() {

    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>> get() = _orders

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    init {
        loadOrders()
    }

    private fun loadOrders() {
        RetrofitClient.instance.getOrders().enqueue(object : Callback<OrdersResponse> {
            override fun onResponse(call: Call<OrdersResponse>, response: Response<OrdersResponse>) {
                if (response.isSuccessful) {
                    _orders.value = response.body()?.orders ?: emptyList()
                } else {
                    _error.value = "Не удалось получить данные: ${response.errorBody()?.string() ?: "Unknown error"}"
                }
            }

            override fun onFailure(call: Call<OrdersResponse>, t: Throwable) {
                _error.value = "Ошибка загрузки заказов: ${t.message}"
            }
        })
    }
}
