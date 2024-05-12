package com.example.extendogames.ui.activites

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.extendogames.R
import com.example.extendogames.api.services.RetrofitClient
import com.example.extendogames.ui.adapters.AdminOrderHistoryAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminOrderHistoryActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdminOrderHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_orders_history)

        recyclerView = findViewById(R.id.orderHistoryRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AdminOrderHistoryAdapter(emptyList())
        recyclerView.adapter = adapter

        loadOrders()
    }

    private fun loadOrders() {
        RetrofitClient.instance.getOrders().enqueue(object : Callback<com.example.extendogames.api.responses.OrdersResponse> {
            override fun onResponse(call: Call<com.example.extendogames.api.responses.OrdersResponse>, response: Response<com.example.extendogames.api.responses.OrdersResponse>) {
                if (response.isSuccessful) {
                    response.body()?.orders?.forEach {
                        Log.d("AdminOrderHistory", "Order date: ${it.order_date}")
                    }
                    adapter.updateData(response.body()?.orders ?: emptyList())
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    Toast.makeText(this@AdminOrderHistoryActivity, "Не удалось получить данные: $errorMessage", Toast.LENGTH_LONG).show()
                }
            }


            override fun onFailure(call: Call<com.example.extendogames.api.responses.OrdersResponse>, t: Throwable) {
                Log.e("AdminOrderHistory", "Error loading orders: ${t.message}")
                Toast.makeText(this@AdminOrderHistoryActivity, "Ошибка загрузки заказов: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}

