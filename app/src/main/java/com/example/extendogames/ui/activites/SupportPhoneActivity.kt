package com.example.extendogames.ui.activites

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.extendogames.R
import com.example.extendogames.ui.viewmodels.SupportPhoneViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SupportPhoneActivity : AppCompatActivity() {
    private lateinit var phoneNumberInput: EditText
    private lateinit var submitButton: Button
    private lateinit var user: FirebaseUser
    private val viewModel: SupportPhoneViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support_phone)

        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()
        }

        phoneNumberInput = findViewById(R.id.phone_input)
        submitButton = findViewById(R.id.submit_button)
        user = FirebaseAuth.getInstance().currentUser!!

        phoneNumberInput.setText("+7 ")
        phoneNumberInput.setSelection(phoneNumberInput.text.length)

        phoneNumberInput.addTextChangedListener(object : TextWatcher {
            private var isFormatting: Boolean = false
            private var deletingHyphen: Boolean = false
            private var deletingBackward: Boolean = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (count > 0 && after == 0) {
                    deletingBackward = true
                    if (s?.get(start) == '-') {
                        deletingHyphen = true
                    }
                } else {
                    deletingBackward = false
                    deletingHyphen = false
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                isFormatting = true

                val formatted = formatPhoneNumber(s.toString())
                s?.replace(0, s.length, formatted, 0, formatted.length)

                isFormatting = false
            }
        })

        submitButton.setOnClickListener {
            val phoneNumber = phoneNumberInput.text.toString().trim()
            val email = user.email
            val name = user.displayName
            if (phoneNumber.isNotEmpty() && email != null && name != null) {
                viewModel.sendPhoneNumber(phoneNumber, email, name)
            } else {
                Toast.makeText(this, "Пожалуйста, введите номер телефона и убедитесь, что вы авторизованы", Toast.LENGTH_SHORT).show()
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

    private fun formatPhoneNumber(phone: String): String {
        val cleaned = phone.replace("[^\\d]".toRegex(), "")
        val sb = StringBuilder()

        if (cleaned.length >= 1) {
            sb.append("+7 ")
        }
        if (cleaned.length >= 4) {
            sb.append("(${cleaned.substring(1, 4)})")
        } else if (cleaned.length > 1) {
            sb.append("(${cleaned.substring(1)})")
        }
        if (cleaned.length >= 7) {
            sb.append(" ${cleaned.substring(4, 7)}")
        } else if (cleaned.length > 4) {
            sb.append(" ${cleaned.substring(4)}")
        }
        if (cleaned.length >= 9) {
            sb.append("-${cleaned.substring(7, 9)}")
        } else if (cleaned.length > 7) {
            sb.append("-${cleaned.substring(7)}")
        }
        if (cleaned.length >= 11) {
            sb.append("-${cleaned.substring(9, 11)}")
        } else if (cleaned.length > 9) {
            sb.append("-${cleaned.substring(9)}")
        }

        return sb.toString()
    }
}
