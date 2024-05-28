package com.example.extendogames.ui.activites

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.extendogames.R
import com.example.extendogames.ui.viewmodels.AdminBalanceViewModel

class AdminBalanceActivity : AppCompatActivity() {

    private lateinit var emailField: EditText
    private lateinit var balanceField: EditText
    private lateinit var updateButton: Button
    private val viewModel: AdminBalanceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_balance)

        emailField = findViewById(R.id.email_field)
        balanceField = findViewById(R.id.balance_field)
        updateButton = findViewById(R.id.update_balance_button)

        updateButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val balance = balanceField.text.toString().trim().toDoubleOrNull()

            if (email.isNotEmpty() && balance != null) {
                viewModel.updateUserBalance(email, balance)
            } else {
                Toast.makeText(this, "Пожалуйста, заполните все поля корректно", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.message.observe(this, Observer { message ->
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        })
    }
}
