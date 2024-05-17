package com.example.extendogames.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _loginResult = MutableLiveData<Result<Boolean>>()
    val loginResult: LiveData<Result<Boolean>> = _loginResult

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _loginResult.value = Result.failure(Exception("Пожалуйста, заполните все поля."))
            return
        }

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid
                userId?.let {
                    firestore.collection("Users").document(it)
                        .get()
                        .addOnSuccessListener { document ->
                            val userPrivileges = document.getBoolean("userPrivileges") ?: false
                            _loginResult.value = Result.success(userPrivileges)
                        }
                        .addOnFailureListener {
                            _loginResult.value = Result.failure(it)
                        }
                }
            } else {
                _loginResult.value = Result.failure(task.exception ?: Exception("Ошибка входа"))
            }
        }
    }
}