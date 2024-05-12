package com.example.extendogames.ui.activites

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.extendogames.R
import com.example.extendogames.api.services.ApiService
import com.example.extendogames.api.models.ReservationRequest
import com.example.extendogames.api.responses.AvailabilityResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupApi()
        checkAllStationsAvailability()

        val isAdmin = intent.getBooleanExtra("isAdmin", false)
        setupButtons(isAdmin)
    }

    private fun setupApi() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    private fun checkAllStationsAvailability() {
        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

        for (i in 1..25) {
            val request = ReservationRequest(
                placeNumber = i.toString(),
                date = todayDate,
                time = currentTime,
                duration = 1,
                userEmail = "qwe@mail.ru",
                userName = "Dmitriy"
            )
            checkStationAvailability(request)
        }
    }

    private fun checkStationAvailability(request: ReservationRequest) {
        val call = apiService.checkAvailability(request)
        call.enqueue(object : Callback<AvailabilityResponse> {
            override fun onResponse(call: Call<AvailabilityResponse>, response: Response<AvailabilityResponse>) {
                val isAvailable = response.isSuccessful && response.body()?.available == true
                updateButtonBackground(request.placeNumber, isAvailable)
            }

            override fun onFailure(call: Call<AvailabilityResponse>, t: Throwable) {
                Log.e("MainActivity", "Error checking availability: ${t.message}")
                updateButtonBackground(request.placeNumber, false) // Предположим, что при ошибке запроса место недоступно
            }
        })
    }

    private fun updateButtonBackground(stationId: String, isAvailable: Boolean) {
        runOnUiThread {
            val buttonId = resources.getIdentifier("button_place_$stationId", "id", packageName)
            val button = findViewById<Button>(buttonId)
            button.setBackgroundResource(if (isAvailable) R.drawable.polygon_22 else R.drawable.arrowright)
        }
    }


    private fun setupButtons(isAdmin: Boolean) {
        for (i in 1..22) {
            val buttonId = resources.getIdentifier("button_place_$i", "id", packageName)
            val button = findViewById<Button>(buttonId)
            button.setOnClickListener {
                openBookingActivity(i)
            }
        }

        setupControlButtons()
        val adminPanelButton = findViewById<Button>(R.id.button_admin_panel)
        adminPanelButton.setOnClickListener {
            startActivity(Intent(this, AdminPanelActivity::class.java))
        }
        adminPanelButton.visibility = if (isAdmin) View.VISIBLE else View.GONE
    }

    private fun setupControlButtons() {
        val profileButton = findViewById<Button>(R.id.button_profile)
        profileButton.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        val newsButton = findViewById<Button>(R.id.button_news)
        newsButton.setOnClickListener {
            startActivity(Intent(this, NewsActivity::class.java))
        }

        val tournamentButton = findViewById<Button>(R.id.button_tournament)
        tournamentButton.setOnClickListener {
            startActivity(Intent(this, TournamentActivity::class.java))
        }

        val menuButton = findViewById<Button>(R.id.button_menu)
        menuButton.setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
        }

        val reviewButton = findViewById<Button>(R.id.button_review)
        reviewButton.setOnClickListener {
            startActivity(Intent(this, ReviewActivity::class.java))
        }
    }

    private fun openBookingActivity(placeNumber: Int) {
        val intent = Intent(this, ReservationActivity::class.java).apply {
            putExtra("PLACE_NUMBER", placeNumber.toString())
        }
        startActivity(intent)
    }
}