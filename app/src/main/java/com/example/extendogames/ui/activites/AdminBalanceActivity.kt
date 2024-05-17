package com.example.extendogames.ui.activites

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.extendogames.R
import com.google.firebase.firestore.FirebaseFirestore

class AdminBalanceActivity : AppCompatActivity() {

    private lateinit var emailField: EditText
    private lateinit var balanceField: EditText
    private lateinit var updateButton: Button

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
                updateUserBalance(email, balance)
            } else {
                Toast.makeText(this, "Пожалуйста, заполните все поля корректно", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateUserBalance(email: String, balance: Double) {
        val firestore = FirebaseFirestore.getInstance()
        val usersRef = firestore.collection("Users")

        usersRef.whereEqualTo("email", email).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        val currentBalance = document.getDouble("balance") ?: 0.0
                        val newBalance = currentBalance + balance
                        usersRef.document(document.id).update("balance", newBalance)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Баланс обновлен", Toast.LENGTH_LONG).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Ошибка при обновлении баланса: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                } else {
                    Toast.makeText(this, "Пользователь не найден", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Ошибка при поиске пользователя: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
