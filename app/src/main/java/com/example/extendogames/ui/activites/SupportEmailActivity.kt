package com.example.extendogames.ui.activites

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.extendogames.R
import com.example.extendogames.ui.viewmodels.SupportEmailViewModel

class SupportEmailActivity : AppCompatActivity() {
    private lateinit var emailInput: EditText
    private lateinit var questionInput: EditText
    private lateinit var sendButton: Button
    private val viewModel: SupportEmailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support_email)

        emailInput = findViewById(R.id.email_input)
        questionInput = findViewById(R.id.question_input)
        sendButton = findViewById(R.id.send_button)

        sendButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val question = questionInput.text.toString().trim()

            if (email.isNotEmpty() && question.isNotEmpty()) {
                viewModel.sendSupportRequest(email, question)
            } else {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
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
