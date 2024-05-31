package com.example.extendogames.ui.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.extendogames.api.models.Order
import com.example.extendogames.api.responses.OrdersResponse
import com.example.extendogames.api.services.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminOrderHistoryViewModel(application: Application) : AndroidViewModel(application) {

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

    fun clearOrderHistory() {
        viewModelScope.launch {
            RetrofitClient.instance.clearOrderHistory().enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        loadOrders()
                        Toast.makeText(getApplication(), "История успешно очищена", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(getApplication(), "Не удалось очистить историю", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(getApplication(), "Ошибка: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
