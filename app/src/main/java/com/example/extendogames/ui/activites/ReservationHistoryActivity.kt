package com.example.extendogames.ui.activites

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.extendogames.R
import com.example.extendogames.ui.adapters.ReservationHistoryAdapter
import com.example.extendogames.ui.viewmodels.ReservationHistoryViewModel

class ReservationHistoryActivity : AppCompatActivity() {

    private val viewModel: ReservationHistoryViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReservationHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_history)

        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.reservationHistoryRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ReservationHistoryAdapter()
        recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.reservations.observe(this, Observer { reservations ->
            adapter.updateData(reservations)
        })

        viewModel.message.observe(this, Observer { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        })
    }
}