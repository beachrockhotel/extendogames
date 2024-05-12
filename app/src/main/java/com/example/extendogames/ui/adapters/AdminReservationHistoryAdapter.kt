package com.example.extendogames.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.extendogames.api.models.ReservationResponse

class AdminReservationHistoryAdapter(private var reservations: List<ReservationResponse>) :
    RecyclerView.Adapter<AdminReservationHistoryAdapter.ReservationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return ReservationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        holder.bind(reservations[position])
    }

    override fun getItemCount() = reservations.size

    class ReservationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(reservation: ReservationResponse) {
            val text = buildString {
                append("ID: ${reservation.reservation_id}\n")
                append("Place: ${reservation.seat_number}\n")
                append("Time: ${reservation.time}\n")
                append("Duration: ${reservation.duration} hours\n")
                append("Email: ${reservation.user_email}\n")
                append("Name: ${reservation.user_name}")
            }
            textView.text = text
        }
    }

    fun updateReservations(newReservations: List<ReservationResponse>) {
        reservations = newReservations
        notifyDataSetChanged()
    }
}

