package com.example.extendogames.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.extendogames.R
import com.example.extendogames.api.models.ReservationResponse

class AdminReservationHistoryAdapter(private var reservations: List<ReservationResponse>) :
    RecyclerView.Adapter<AdminReservationHistoryAdapter.ReservationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.admin_reservation_item, parent, false)
        return ReservationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        holder.bind(reservations[position])
    }

    override fun getItemCount() = reservations.size

    class ReservationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val idTextView: TextView = itemView.findViewById(R.id.reservation_id)
        private val placeTextView: TextView = itemView.findViewById(R.id.reservation_place)
        private val timeTextView: TextView = itemView.findViewById(R.id.reservation_time)
        private val durationTextView: TextView = itemView.findViewById(R.id.reservation_duration)
        private val emailTextView: TextView = itemView.findViewById(R.id.reservation_email)
        private val nameTextView: TextView = itemView.findViewById(R.id.reservation_name)

        fun bind(reservation: ReservationResponse) {
            idTextView.text = "ID: ${reservation.reservation_id}"
            placeTextView.text = "Место: ${reservation.seat_number}"
            timeTextView.text = "Время: ${reservation.time}"
            durationTextView.text = "Длительность: ${reservation.duration} часов"
            emailTextView.text = "Почта: ${reservation.user_email}"
            nameTextView.text = "Имя: ${reservation.user_name}"
        }
    }

    fun updateReservations(newReservations: List<ReservationResponse>) {
        reservations = newReservations
        notifyDataSetChanged()
    }
}
