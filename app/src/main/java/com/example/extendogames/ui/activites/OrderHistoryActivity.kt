package com.example.extendogames.ui.activites

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.extendogames.R
import com.example.extendogames.api.services.RetrofitClient
import com.example.extendogames.api.models.Order
import com.example.extendogames.ui.adapters.OrderHistoryAdapter
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderHistoryActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OrderHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)

        recyclerView = findViewById(R.id.orderHistoryRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = OrderHistoryAdapter()
        recyclerView.adapter = adapter

        loadOrders()
    }

    private fun loadOrders() {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: return
        RetrofitClient.instance.getUserOrders(userEmail).enqueue(object : Callback<List<Order>> {
            override fun onResponse(call: Call<List<Order>>, response: Response<List<Order>>) {
                if (response.isSuccessful) {
                    val orders = response.body() ?: emptyList()
                    Log.d("UpdateData", "Updating data with ${orders.size} new orders.")
                    orders.forEach { order ->
                        Log.d("UpdateData", "Order id with ${order.items.size} items.")
                    }
                    adapter.updateData(orders)
                } else {
                    Toast.makeText(applicationContext, "Ошибка загрузки данных: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Order>>, t: Throwable) {
                Toast.makeText(applicationContext, "Ошибка подключения: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}