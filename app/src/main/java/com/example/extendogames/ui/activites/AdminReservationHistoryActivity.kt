package com.example.extendogames.ui.activites

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.extendogames.R
import com.example.extendogames.api.models.ReservationResponse
import com.example.extendogames.api.services.RetrofitClient
import com.example.extendogames.ui.adapters.AdminReservationHistoryAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminReservationHistoryActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdminReservationHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_reservation_history)

        recyclerView = findViewById(R.id.reservationRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AdminReservationHistoryAdapter(emptyList())
        recyclerView.adapter = adapter

        loadReservations()
    }



    private fun loadReservations() {
        RetrofitClient.instance.getReservations().enqueue(object : Callback<List<ReservationResponse>> {
            override fun onResponse(call: Call<List<ReservationResponse>>, response: Response<List<ReservationResponse>>) {
                if (response.isSuccessful) {
                    adapter.updateReservations(response.body() ?: emptyList())
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("AdminResHistoryActivity", "Failed to retrieve data: $errorMessage")
                    Toast.makeText(this@AdminReservationHistoryActivity, "Ошибка получения данных: $errorMessage", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<ReservationResponse>>, t: Throwable) {
                Log.e("AdminResHistoryActivity", "Network error: ${t.message}", t)
                Toast.makeText(this@AdminReservationHistoryActivity, "Ошибка сети: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
