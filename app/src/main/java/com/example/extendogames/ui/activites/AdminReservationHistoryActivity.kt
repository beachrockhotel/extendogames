package com.example.extendogames.ui.activites

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.extendogames.R
import com.example.extendogames.api.models.ReservationResponse
import com.example.extendogames.ui.adapters.AdminReservationHistoryAdapter
import com.example.extendogames.ui.viewmodels.AdminReservationHistoryViewModel

class AdminReservationHistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdminReservationHistoryAdapter
    private val viewModel: AdminReservationHistoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_reservation_history)

        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()
        }

        recyclerView = findViewById(R.id.reservationRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AdminReservationHistoryAdapter(emptyList(), { reservation ->
            deleteReservation(reservation)
        }, { reservationId, isChecked ->
            // Удалите этот вызов, если он больше не нужен
            // viewModel.updateAttendance(reservationId, isChecked)
        })
        recyclerView.adapter = adapter

        val clearHistoryButton = findViewById<Button>(R.id.clear_history_button)
        clearHistoryButton.setOnClickListener {
            clearHistory()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.reservations.observe(this, Observer { reservations ->
            adapter.updateReservations(reservations)
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
                viewModel.clearReservationHistory()
            }
            .setNegativeButton("Нет", null)
            .show()
    }

    private fun deleteReservation(reservation: ReservationResponse) {
        AlertDialog.Builder(this)
            .setTitle("Удалить бронирование")
            .setMessage("Вы уверены, что хотите удалить это бронирование?")
            .setPositiveButton("Да") { _, _ ->
                viewModel.deleteReservation(reservation.reservation_id)
            }
            .setNegativeButton("Нет", null)
            .show()
    }
}
