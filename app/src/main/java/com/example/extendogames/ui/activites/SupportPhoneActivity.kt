package com.example.extendogames.ui.activites

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.extendogames.R
import com.example.extendogames.ui.viewmodels.SupportPhoneViewModel

class SupportPhoneActivity : AppCompatActivity() {
    private lateinit var phoneNumberInput: EditText
    private lateinit var submitButton: Button
    private val viewModel: SupportPhoneViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support_phone)

        phoneNumberInput = findViewById(R.id.phone_input)
        submitButton = findViewById(R.id.submit_button)

        submitButton.setOnClickListener {
            val phoneNumber = phoneNumberInput.text.toString().trim()
            if (phoneNumber.isNotEmpty()) {
                viewModel.sendPhoneNumber(phoneNumber)
            } else {
                Toast.makeText(this, "Пожалуйста, введите номер телефона", Toast.LENGTH_SHORT).show()
            }
        }

        setupObservers()
    }

    private fun setupObservers() {
        viewModel.message.observe(this, Observer { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        })
    }
}
