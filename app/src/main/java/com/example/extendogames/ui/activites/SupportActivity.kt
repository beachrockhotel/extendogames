package com.example.extendogames.ui.activites

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.extendogames.R

class SupportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support)

        val supportEmailButton = findViewById<Button>(R.id.support_email_button)
        supportEmailButton.setOnClickListener {
            val intent = Intent(this, SupportEmailActivity::class.java)
            startActivity(intent)
        }

        val supportPhoneButton = findViewById<Button>(R.id.support_phone_button)
        supportPhoneButton.setOnClickListener {
            val intent = Intent(this, SupportPhoneActivity::class.java)
            startActivity(intent)
        }
    }
}
