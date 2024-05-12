package com.example.extendogames.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.extendogames.api.models.ReservationResponse

class ReservationAdapter : RecyclerView.Adapter<ReservationAdapter.ViewHolder>() {
    private var reservations: List<ReservationResponse> = listOf()

    fun updateData(newReservations: List<ReservationResponse>) {
        reservations = newReservations
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(reservations[position])
    }

    override fun getItemCount(): Int = reservations.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(reservation: ReservationResponse) {
            textView.text = "Место ${reservation.seat_number} было забронировано в ${reservation.time} ${reservation.date} на ${reservation.duration} часов"
        }
    }
}
