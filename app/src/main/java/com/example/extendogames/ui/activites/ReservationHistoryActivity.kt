package com.example.extendogames.ui.activites

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.extendogames.R
import com.example.extendogames.api.services.ApiService
import com.example.extendogames.api.models.ReservationResponse
import com.example.extendogames.ui.adapters.ReservationAdapter
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ReservationHistoryActivity : AppCompatActivity() {
    private lateinit var apiService: ApiService
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReservationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_history)

        setupApi()
        setupRecyclerView()
        loadReservations()
    }

    private fun setupApi() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.reservationHistoryRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ReservationAdapter()
        recyclerView.adapter = adapter
    }


    private fun loadReservations() {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: return
        apiService.getUserReservations(userEmail).enqueue(object : Callback<List<ReservationResponse>> {
            override fun onResponse(call: Call<List<ReservationResponse>>, response: Response<List<ReservationResponse>>) {
                if (response.isSuccessful) {
                    response.body()?.let { reservations ->
                        reservations.forEach {
                            Log.d("ReservationData", "Loaded: ${it.seat_number}")
                        }
                        adapter.updateData(reservations)
                    }
                } else {
                    Toast.makeText(this@ReservationHistoryActivity, "Ошибка загрузки данных: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<ReservationResponse>>, t: Throwable) {
                Toast.makeText(this@ReservationHistoryActivity, "Ошибка подключения: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
