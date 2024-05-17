package com.example.extendogames.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.extendogames.api.models.CartItem
import com.example.extendogames.api.models.Order
import com.example.extendogames.api.responses.OrderResponse
import com.example.extendogames.api.services.ApiService
import com.example.extendogames.managers.CartManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartViewModel(private val apiService: ApiService) : ViewModel() {
    private val _cartItems = MutableLiveData<List<CartItem>>().apply {
        value = CartManager.getCartItems()
    }
    val cartItems: LiveData<List<CartItem>> = _cartItems

    private val _totalPrice = MutableLiveData<Double>().apply {
        value = CartManager.getTotalPrice()
    }
    val totalPrice: LiveData<Double> = _totalPrice

    private val _itemsCount = MutableLiveData<Int>().apply {
        value = CartManager.getCartItems().size
    }
    val itemsCount: LiveData<Int> = _itemsCount

    fun updateCart() {
        _cartItems.value = CartManager.getCartItems()
        updateTotalPriceAndCount()
    }

    private fun updateTotalPriceAndCount() {
        _totalPrice.value = CartManager.getTotalPrice()
        _itemsCount.value = CartManager.getCartItems().size
    }

    fun clearCart() {
        CartManager.clearCart()
        updateCart()
    }

    fun createOrder(order: Order, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val call = apiService.createOrder(order)
        call.enqueue(object : Callback<OrderResponse> {
            override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    CartManager.clearCart()
                    updateCart()
                    onSuccess()
                } else {
                    onError("Ошибка оформления заказа: ${response.code()} ${response.message()}")
                }
            }

            override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                onError("Ошибка подключения: ${t.message}")
            }
        })
    }
}
