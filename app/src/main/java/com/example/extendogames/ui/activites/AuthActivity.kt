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
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.extendogames.R
import com.example.extendogames.ui.viewmodels.AuthViewModel

class AuthActivity : AppCompatActivity() {
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val emailField = findViewById<EditText>(R.id.login_field_auth)
        val passwordField = findViewById<EditText>(R.id.password_field_auth)
        val loginButton = findViewById<Button>(R.id.login_button)

        loginButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            viewModel.login(email, password)
        }

        viewModel.loginResult.observe(this, Observer { result ->
            result.onSuccess { userPrivileges ->
                val intent = Intent(this@AuthActivity, MainActivity::class.java).apply {
                    putExtra("userPrivileges", userPrivileges)
                }
                startActivity(intent)
                finish()
            }.onFailure { exception ->
                Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
            }
        })

        setupRegistrationLink()
    }

    private fun setupRegistrationLink() {
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
