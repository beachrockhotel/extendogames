package com.example.extendogames.ui.activites

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.extendogames.R
import com.example.extendogames.ui.adapters.AdminReservationHistoryAdapter
import com.example.extendogames.ui.viewmodels.AdminReservationHistoryViewModel

class AdminReservationHistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdminReservationHistoryAdapter
    private val viewModel: AdminReservationHistoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_reservation_history)

        recyclerView = findViewById(R.id.reservationRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AdminReservationHistoryAdapter(emptyList())
        recyclerView.adapter = adapter

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
}
