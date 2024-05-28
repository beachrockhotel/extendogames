package com.example.extendogames.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class AdminBalanceViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    fun updateUserBalance(email: String, balance: Double) {
        val usersRef = firestore.collection("Users")

        usersRef.whereEqualTo("email", email).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        val currentBalance = document.getDouble("balance") ?: 0.0
                        val newBalance = currentBalance + balance
                        usersRef.document(document.id).update("balance", newBalance)
                            .addOnSuccessListener {
                                _message.value = "Баланс обновлен"
                            }
                            .addOnFailureListener { e ->
                                _message.value = "Ошибка при обновлении баланса: ${e.message}"
                            }
                    }
                } else {
                    _message.value = "Пользователь не найден"
                }
            }
            .addOnFailureListener { e ->
                _message.value = "Ошибка при поиске пользователя: ${e.message}"
            }
    }
}
