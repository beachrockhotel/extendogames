package com.example.extendogames.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.extendogames.R
import com.example.extendogames.api.models.Order
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class AdminOrderHistoryAdapter(private var orders: List<Order>) :
    RecyclerView.Adapter<AdminOrderHistoryAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.admin_order_item, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tableNumberTextView: TextView = itemView.findViewById(R.id.tableNumberTextView)
        private val userNameTextView: TextView = itemView.findViewById(R.id.userNameTextView)
        private val userEmailTextView: TextView = itemView.findViewById(R.id.userEmailTextView)
        private val totalPriceTextView: TextView = itemView.findViewById(R.id.totalPriceTextView)
        private val itemsDetailTextView: TextView = itemView.findViewById(R.id.itemsDetailTextView)

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
        }

        private fun formatDate(dateString: String?): String {
            if (dateString == null) {
                Log.e("OrderHistoryAdapter", "Provided date string is null")
                return "Дата не указана"
            }

            return try {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
                val dateTime = LocalDateTime.parse(dateString, formatter)
                dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
            } catch (e: DateTimeParseException) {
                Log.e("OrderHistoryAdapter", "Error formatting date: ${e.message}", e)
                "Некорректная дата"
            }
        }
    }

    fun updateData(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }
}
