package com.example.extendogames.ui.activites

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.extendogames.R
import com.example.extendogames.api.services.ApiService
import com.example.extendogames.ui.factory.MainViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory(apiService)
    }

    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupApi()
        setupButtons()

        val userPrivileges = intent.getBooleanExtra("userPrivileges", false)
        setupAdminButton(userPrivileges)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
    }

    override fun onResume() {
        super.onResume()
        updateButtonsState()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.navigation_home
    }


    private fun setupApi() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    private fun setupButtons() {
        for (i in 1..35) {
            val buttonId = resources.getIdentifier("button_place_$i", "id", packageName)
            val button = findViewById<Button>(buttonId)
            button?.setOnClickListener {
                if (i == 24) {
                    openBookingActivity(i, "PlayStation", "PlayStation 5, 2 геймпада, Игры: FIFA 2024, Uncharted, Spider-Man 2, UFC 5, The Last Of US 1, 2 части")
                } else if (i == 25) {
                    openBookingActivity(i, "Xbox", "XBOX SERIES X, 2 геймпада, Игры: Halo, Forza, Gears of War, FIFA 2024, Uncharted, Spider-Man 2, UFC 5")
                } else {
                    openBookingActivity(i, "Компьютер", "Intel Core i5-10400F, NVIDIA GeForce RTX 2070 Super 8 Gb, 16 Gb DDR4, SSD+HDD, Монитор: ASUS 24,5, 165Гц, Интернет: 500 МБит/с, Игры: CS2, DOTA2, PUBG")
                }
            }
        }
    }

    private fun updateButtonsState() {
        for (i in 1..25) {
            mainViewModel.checkStationAvailability(i.toString()) { stationId, isAvailable ->
                updateButtonBackground(stationId, isAvailable)
            }
        }
    }

    private fun setupAdminButton(userPrivileges: Boolean) {
        val adminPanelButton = findViewById<Button>(R.id.button_admin_panel)
        adminPanelButton.visibility = if (userPrivileges) View.VISIBLE else View.GONE
        adminPanelButton.setOnClickListener {
            val intent = Intent(this, AdminPanelActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
        }
    }

    private fun updateButtonBackground(stationId: String, isAvailable: Boolean) {
        runOnUiThread {
            val buttonId = resources.getIdentifier("button_place_$stationId", "id", packageName)
            val button = findViewById<Button>(buttonId)
            button.setBackgroundResource(if (isAvailable) R.drawable.polygon_22 else R.drawable.busy)
        }
    }

    private fun openBookingActivity(placeNumber: Int, deviceType: String, deviceSpecs: String) {
        val intent = Intent(this, ReservationActivity::class.java).apply {
            putExtra("PLACE_NUMBER", placeNumber.toString())
            putExtra("DEVICE_TYPE", deviceType)
            putExtra("DEVICE_SPECS", deviceSpecs)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val userPrivileges = intent.getBooleanExtra("userPrivileges", false)
        when (item.itemId) {
            R.id.navigation_home -> {
                return true
            }
            R.id.navigation_news -> {
                val intent = Intent(this, NewsActivity::class.java)
                intent.putExtra("userPrivileges", userPrivileges)
                startActivity(intent)
                return true
            }
            R.id.navigation_menu -> {
                val intent = Intent(this, MenuActivity::class.java)
                intent.putExtra("userPrivileges", userPrivileges)
                startActivity(intent)
                return true
            }
            R.id.navigation_tournaments -> {
                val intent = Intent(this, TournamentActivity::class.java)
                intent.putExtra("userPrivileges", userPrivileges)
                startActivity(intent)
                return true
            }
            R.id.navigation_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra("userPrivileges", userPrivileges)
                startActivity(intent)
                return true
            }
        }
        return false
    }

}
