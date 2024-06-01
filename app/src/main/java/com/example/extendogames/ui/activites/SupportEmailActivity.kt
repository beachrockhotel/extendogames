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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class SupportEmailActivity : AppCompatActivity() {
    private lateinit var emailInput: EditText
    private lateinit var questionInput: EditText
    private lateinit var sendButton: Button
    private lateinit var user: FirebaseUser
    private val viewModel: SupportEmailViewModel by viewModels()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support_email)

        emailInput = findViewById(R.id.email_input)
        questionInput = findViewById(R.id.question_input)
        sendButton = findViewById(R.id.send_button)
        user = FirebaseAuth.getInstance().currentUser!!

        firestore.collection("Users").document(user.uid).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val email = document.getString("email")
                    emailInput.setText(email)
                } else {
                    Toast.makeText(this, "Не удалось загрузить email", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Ошибка загрузки email: ${it.message}", Toast.LENGTH_SHORT).show()
            }

        sendButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val question = questionInput.text.toString().trim()
            val name = user.displayName

            if (email.isNotEmpty() && question.isNotEmpty() && name != null) {
                viewModel.sendSupportRequest(email, question, name)
            } else {
                Toast.makeText(this, "Заполните все поля и убедитесь, что вы авторизованы", Toast.LENGTH_SHORT).show()
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
