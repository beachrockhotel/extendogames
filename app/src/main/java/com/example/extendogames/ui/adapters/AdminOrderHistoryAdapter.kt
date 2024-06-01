package com.example.extendogames.ui.adapters

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.extendogames.R
import com.example.extendogames.api.models.Order

class AdminOrderHistoryAdapter(
    private var orders: List<Order>,
    private val onDeleteClick: (Order) -> Unit
) : RecyclerView.Adapter<AdminOrderHistoryAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.admin_order_item, parent, false)
        return OrderViewHolder(view, parent.context, onDeleteClick)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size

    class OrderViewHolder(itemView: View, context: Context, private val onDeleteClick: (Order) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val tableNumberTextView: TextView = itemView.findViewById(R.id.tableNumberTextView)
        private val userNameTextView: TextView = itemView.findViewById(R.id.userNameTextView)
        private val userEmailTextView: TextView = itemView.findViewById(R.id.userEmailTextView)
        private val totalPriceTextView: TextView = itemView.findViewById(R.id.totalPriceTextView)
        private val itemsDetailTextView: TextView = itemView.findViewById(R.id.itemsDetailTextView)
        private val deleteButton: Button = itemView.findViewById(R.id.delete_order_button)
        private val completedCheckBox: CheckBox = itemView.findViewById(R.id.completed_checkbox)

        private val sharedPreferences: SharedPreferences = context.getSharedPreferences("OrderPreferences", Context.MODE_PRIVATE)

        fun bind(order: Order) {
            tableNumberTextView.text = "Стол: ${order.table_number}"
            userNameTextView.text = "Имя: ${order.user_name}"
            userEmailTextView.text = "Почта: ${order.user_email}"
            totalPriceTextView.text = "Сумма: ${order.total_price} руб"
            itemsDetailTextView.text = buildString {
                append("Товары:\n")
                order.items.forEach { item ->
                    append("${item.name} - ${item.quantity} x ${item.price} руб\n")
                }
            }

            // Load saved checkbox state
            val isChecked = sharedPreferences.getBoolean("${order.user_email}_${order.table_number}", false)
            completedCheckBox.isChecked = isChecked

            // Save checkbox state
            completedCheckBox.setOnCheckedChangeListener { _, isChecked ->
                sharedPreferences.edit().putBoolean("${order.user_email}_${order.table_number}", isChecked).apply()
            }

            deleteButton.setOnClickListener {
                onDeleteClick(order)
            }
        }
    }

    fun updateData(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }
}
