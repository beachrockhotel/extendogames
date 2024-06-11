package com.example.extendogames.ui.activites

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.extendogames.R
import com.example.extendogames.ui.factory.RegistrationViewModelFactory
import com.example.extendogames.ui.viewmodels.RegistrationViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistrationActivity : AppCompatActivity() {
    private val viewModel: RegistrationViewModel by viewModels {
        RegistrationViewModelFactory(
            FirebaseAuth.getInstance(),
            FirebaseFirestore.getInstance()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()
        }

        val nameField = findViewById<EditText>(R.id.name_registration)
        val emailField = findViewById<EditText>(R.id.email_reg)
        val passwordField = findViewById<EditText>(R.id.password)
        val repeatPasswordField = findViewById<EditText>(R.id.repeat_password)
        val registerButton = findViewById<Button>(R.id.register_button)

        registerButton.setOnClickListener {
            val name = nameField.text.toString().trim()
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            val repeatPassword = repeatPasswordField.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, заполните все поля.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Введите корректный email адрес.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (!isPasswordValid(password)) {
                Toast.makeText(this, "Пароль должен содержать не менее 8 символов, включать верхний и нижний регистры, а также специальные символы.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (password != repeatPassword) {
                Toast.makeText(this, "Пароли не совпадают.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            viewModel.registerUser(name, email, password, false, {
                Toast.makeText(this, "Пользователь зарегистрирован успешно.", Toast.LENGTH_LONG).show()
                val authActivityIntent = Intent(this, AuthActivity::class.java)
                startActivity(authActivityIntent)
                finish()
            }, { error ->
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            })
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$"
        return password.matches(passwordPattern.toRegex())
    }
}
