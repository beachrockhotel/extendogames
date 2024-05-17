package com.example.extendogames.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.extendogames.api.models.MenuItem
import com.example.extendogames.api.models.MenuResponse
import com.example.extendogames.api.services.ApiService
import com.example.extendogames.managers.CartManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuViewModel(private val apiService: ApiService) : ViewModel() {

    private val _menuItems = MutableLiveData<List<MenuItem>>()
    val menuItems: LiveData<List<MenuItem>> = _menuItems

    fun loadMenuItems() {
        apiService.getMenuItems().enqueue(object : Callback<MenuResponse> {
            override fun onResponse(call: Call<MenuResponse>, response: Response<MenuResponse>) {
                if (response.isSuccessful) {
                    _menuItems.postValue(response.body()?.menu ?: emptyList())
                } else {
                    _menuItems.postValue(emptyList())
                }
            }

            override fun onFailure(call: Call<MenuResponse>, t: Throwable) {
                _menuItems.postValue(emptyList())
            }
        })
    }

    fun addToCart(menuItem: MenuItem, quantity: Int) {
        CartManager.addToCart(menuItem, quantity)
    }
}
