package com.example.extendogames.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.extendogames.R
import com.example.extendogames.api.models.ReservationResponse

class AdminReservationHistoryAdapter(
    private var reservations: List<ReservationResponse>,
    private val onDeleteClick: (ReservationResponse) -> Unit,
    private val onAttendanceChecked: (Int, Boolean) -> Unit
) : RecyclerView.Adapter<AdminReservationHistoryAdapter.ReservationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.admin_reservation_item, parent, false)
        return ReservationViewHolder(view, onDeleteClick, onAttendanceChecked)
    }

    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        holder.bind(reservations[position])
    }

    override fun getItemCount() = reservations.size

    class ReservationViewHolder(
        itemView: View,
        private val onDeleteClick: (ReservationResponse) -> Unit,
        private val onAttendanceChecked: (Int, Boolean) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val idTextView: TextView = itemView.findViewById(R.id.reservation_id)
        private val placeTextView: TextView = itemView.findViewById(R.id.reservation_place)
        private val timeTextView: TextView = itemView.findViewById(R.id.reservation_time)
        private val durationTextView: TextView = itemView.findViewById(R.id.reservation_duration)
        private val emailTextView: TextView = itemView.findViewById(R.id.reservation_email)
        private val nameTextView: TextView = itemView.findViewById(R.id.reservation_name)
        private val attendanceCheckBox: CheckBox = itemView.findViewById(R.id.attendance_check_box)
        private val deleteButton: Button = itemView.findViewById(R.id.delete_reservation_button)

        fun bind(reservation: ReservationResponse) {
            idTextView.text = "ID: ${reservation.reservation_id}"
            placeTextView.text = "Место: ${reservation.seat_number}"
            timeTextView.text = "Время: ${reservation.time}"
            durationTextView.text = "Длительность: ${reservation.duration} часов"
            emailTextView.text = "Почта: ${reservation.user_email}"
            nameTextView.text = "Имя: ${reservation.user_name}"

            val sharedPreferences = itemView.context.getSharedPreferences("AttendancePrefs", Context.MODE_PRIVATE)
            val isChecked = sharedPreferences.getBoolean("reservation_${reservation.reservation_id}", false)
            attendanceCheckBox.isChecked = isChecked

            attendanceCheckBox.setOnCheckedChangeListener { _, isChecked ->
                onAttendanceChecked(reservation.reservation_id, isChecked)
                sharedPreferences.edit().putBoolean("reservation_${reservation.reservation_id}", isChecked).apply()
            }

            deleteButton.setOnClickListener {
                onDeleteClick(reservation)
            }
        }
    }

    fun updateReservations(newReservations: List<ReservationResponse>) {
        reservations = newReservations
        notifyDataSetChanged()
    }
}