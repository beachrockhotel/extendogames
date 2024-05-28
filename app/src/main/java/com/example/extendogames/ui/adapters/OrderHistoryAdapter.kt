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

class OrderHistoryAdapter(private var orders: MutableList<Order> = mutableListOf()) :
    RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount() = orders.size

    fun updateData(newOrders: List<Order>) {
        Log.d("OrderHistory", "Updating data with ${newOrders.size} orders.")
        orders.clear()
        orders.addAll(newOrders)
        notifyDataSetChanged()
    }

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val summaryTextView: TextView = itemView.findViewById(R.id.order_summary)
        private val detailsTextView: TextView = itemView.findViewById(R.id.order_details)

        fun bind(order: Order) {
            val itemDetails = order.items.joinToString(separator = "\n") { item ->
                "${item.name} - ${item.quantity} x ${item.price} руб."
            }
            summaryTextView.text =
                "Заказ на сумму ${order.total_price} руб. От ${formatDate(order.order_date)}"
            detailsTextView.text = itemDetails
        }

        private fun formatDate(dateString: String?): String {
            return try {
                val formatter =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                val dateTime = LocalDateTime.parse(dateString, formatter)
                dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
            } catch (e: DateTimeParseException) {
                Log.e("Formatting Error", "Error formatting date: ${e.message}")
                "Дата не указана"
            }
        }
    }
}
