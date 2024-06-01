package com.example.extendogames.ui.activites

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.extendogames.R
import com.example.extendogames.api.models.Order
import com.example.extendogames.ui.adapters.AdminOrderHistoryAdapter
import com.example.extendogames.ui.viewmodels.AdminOrderHistoryViewModel

class AdminOrderHistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdminOrderHistoryAdapter
    private val viewModel: AdminOrderHistoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_orders_history)

        recyclerView = findViewById(R.id.orderHistoryRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AdminOrderHistoryAdapter(emptyList()) { order ->
            deleteOrder(order)
        }
        recyclerView.adapter = adapter

        val clearHistoryButton = findViewById<Button>(R.id.clear_history_button)
        clearHistoryButton.setOnClickListener {
            clearHistory()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.orders.observe(this, Observer { orders ->
            adapter.updateData(orders)
        })

        viewModel.error.observe(this, Observer { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        })
    }

    private fun clearHistory() {
        AlertDialog.Builder(this)
            .setTitle("Очистить историю")
            .setMessage("Вы уверены, что хотите очистить историю?")
            .setPositiveButton("Да") { _, _ ->
                viewModel.clearOrderHistory()
            }
            .setNegativeButton("Нет", null)
            .show()
    }

    private fun deleteOrder(order: Order) {
        AlertDialog.Builder(this)
            .setTitle("Удалить заказ")
            .setMessage("Вы уверены, что хотите удалить этот заказ?")
            .setPositiveButton("Да") { _, _ ->
                viewModel.deleteOrder(order.user_email, order.table_number)
            }
            .setNegativeButton("Нет", null)
            .show()
    }
}
