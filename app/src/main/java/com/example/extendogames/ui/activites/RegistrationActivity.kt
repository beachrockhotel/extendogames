package com.example.extendogames.ui.activites

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.extendogames.R
import com.example.extendogames.api.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistrationActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val nameField = findViewById<EditText>(R.id.name_registration)
        val emailField = findViewById<EditText>(R.id.email_reg)
        val passwordField = findViewById<EditText>(R.id.password)
        val repeatPasswordField = findViewById<EditText>(R.id.repeat_password)
        val registerButton = findViewById<Button>(R.id.register_button)

        registerButton.setOnClickListener {
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

            if (password.length < 6) {
                Toast.makeText(this, "Пароль должен содержать минимум 6 символов.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (password != repeatPassword) {
                Toast.makeText(this, "Пароли не совпадают.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userId = user?.uid

                    val userInfo = User(name = nameField.text.toString(), email = email)

                    userId?.let {
                        firestore.collection("Users").document(it).set(userInfo)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this, "Пользователь зарегистрирован успешно.", Toast.LENGTH_LONG).show()
                                    val authActivityIntent = Intent(this@RegistrationActivity, AuthActivity::class.java)
                                    startActivity(authActivityIntent)
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Ошибка сохранения данных: ${task.exception?.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Ошибка регистрации: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        val loginLinkText = findViewById<TextView>(R.id.textView_login_link)
        val spannable = SpannableString(loginLinkText.text)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@RegistrationActivity, AuthActivity::class.java))
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = getColor(R.color.purple)
                ds.isUnderlineText = false
            }
        }

        val start = spannable.indexOf("Войти")
        spannable.setSpan(clickableSpan, start, start + "Войти".length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        loginLinkText.text = spannable
        loginLinkText.movementMethod = LinkMovementMethod.getInstance()
    }
}
