package com.example.extendogames.ui.activites

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.extendogames.R
import com.example.extendogames.ui.adapters.OrderHistoryAdapter
import com.example.extendogames.ui.viewmodels.OrderHistoryViewModel

class OrderHistoryActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OrderHistoryAdapter
    private val viewModel: OrderHistoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)

        recyclerView = findViewById(R.id.orderHistoryRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = OrderHistoryAdapter()
        recyclerView.adapter = adapter

        viewModel.orders.observe(this, Observer { orders ->
            adapter.updateData(orders)
        })

        viewModel.errorMessage.observe(this, Observer { message ->
            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
        })

        viewModel.loadOrders()
    }
}