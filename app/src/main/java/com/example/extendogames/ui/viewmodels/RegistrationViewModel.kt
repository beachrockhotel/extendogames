package com.example.extendogames.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.extendogames.api.models.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegistrationViewModel(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    fun registerUser(name: String, email: String, password: String, userPrivileges: Boolean = false, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                val user = auth.currentUser
                val userInfo = User(email = email, name = name, userPrivileges = userPrivileges, balance = 0.0)
                firestore.collection("Users").document(user!!.uid).set(userInfo).await()
                onSuccess()
            } catch (e: Exception) {
                onError("Ошибка регистрации: ${e.message}")
            }
        }
    }
}