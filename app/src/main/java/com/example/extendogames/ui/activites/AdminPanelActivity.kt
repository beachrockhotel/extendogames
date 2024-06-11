package com.example.extendogames.ui.activites

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.extendogames.R

class AdminPanelActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_panel)

        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()
        }

        val historyAdminButton = findViewById<Button>(R.id.users_reservation_button)
        historyAdminButton.setOnClickListener {
            val intent = Intent(this, AdminReservationHistoryActivity::class.java)
            startActivity(intent)
        }

        val orderAdminButton = findViewById<Button>(R.id.users_order_button)
        orderAdminButton.setOnClickListener {
            val intent = Intent(this, AdminOrderHistoryActivity::class.java)
            startActivity(intent)
        }

        val supportEmailButton = findViewById<Button>(R.id.support_email_button)
        supportEmailButton.setOnClickListener {
            val intent = Intent(this, AdminSupportEmailActivity::class.java)
            startActivity(intent)
        }

        val supportPhoneButton = findViewById<Button>(R.id.support_phone_button)
        supportPhoneButton.setOnClickListener {
            val intent = Intent(this, AdminSupportPhoneActivity::class.java)
            startActivity(intent)
        }

        val balanceAdminButton = findViewById<Button>(R.id.balance_admin_button)
        balanceAdminButton.setOnClickListener {
            val intent = Intent(this, AdminBalanceActivity::class.java)
            startActivity(intent)
        }

        val reservationStatisticsAdminButton = findViewById<Button>(R.id.reservation_statistics_admin_button)
        reservationStatisticsAdminButton.setOnClickListener {
            val intent = Intent(this, AdminReservationStatisticsActivity::class.java)
            startActivity(intent)
        }

        val foodOrderStatisticsAdminButton = findViewById<Button>(R.id.food_order_statistics_admin_button)
        foodOrderStatisticsAdminButton.setOnClickListener {
            val intent = Intent(this, AdminFoodOrderStatisticsActivity::class.java)
            startActivity(intent)
        }
    }
}

