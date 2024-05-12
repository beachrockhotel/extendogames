package com.example.extendogames.ui.activites

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.extendogames.R

class AdminPanelActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_panel)

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
            val intent = Intent(this, AdminSupportListActivity::class.java)
            startActivity(intent)
        }

        val supportPhoneButton = findViewById<Button>(R.id.support_phone_button)
        supportPhoneButton.setOnClickListener {
            val intent = Intent(this, AdminSupportPhoneActivity::class.java)
            startActivity(intent)
        }
    }
}
