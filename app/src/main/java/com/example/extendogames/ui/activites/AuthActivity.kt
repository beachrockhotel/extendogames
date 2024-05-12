package com.example.extendogames.ui.activites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        auth = FirebaseAuth.getInstance()

        val emailField = findViewById<EditText>(R.id.login_field_auth)
        val passwordField = findViewById<EditText>(R.id.password_field_auth)
        val loginButton = findViewById<Button>(R.id.login_button)

        loginButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, заполните все поля.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    userId?.let {
                        FirebaseFirestore.getInstance().collection("Users").document(it)
                            .get()
                            .addOnSuccessListener { document ->
                                val isAdmin = document.getBoolean("isAdmin") ?: false
                                val intent = Intent(this@AuthActivity, MainActivity::class.java).apply {
                                    putExtra("isAdmin", isAdmin)
                                }
                                startActivity(intent)
                                finish()
                            }
                    }
                } else {
                    // Ошибка входа
                    Toast.makeText(this, "Ошибка входа: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }

        }


        val registrationLinkText = findViewById<TextView>(R.id.textView_registration_link)
        val spannable = SpannableString(registrationLinkText.text)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@AuthActivity, RegistrationActivity::class.java))
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = getColor(R.color.purple)
                ds.isUnderlineText = false
            }
        }

        val start = spannable.indexOf("Регистрация")
        spannable.setSpan(clickableSpan, start, start + "Регистрация".length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        registrationLinkText.text = spannable
        registrationLinkText.movementMethod = LinkMovementMethod.getInstance()
    }
}