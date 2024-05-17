package com.example.extendogames.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> = _userEmail

    private val _profileImageUrl = MutableLiveData<String?>()
    val profileImageUrl: LiveData<String?> = _profileImageUrl

    private val _balance = MutableLiveData<Double>()
    val balance: LiveData<Double> = _balance

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        loadProfileFromFirestore()
    }

    fun loadProfileFromFirestore() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("Users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        _userName.value = document.getString("name") ?: "Имя не указано"
                        _userEmail.value = auth.currentUser?.email ?: "Email не указан"
                        _profileImageUrl.value = auth.currentUser?.photoUrl?.toString()
                        _balance.value = document.getDouble("balance") ?: 0.0
                    } else {
                        _error.value = "Пользователь не найден"
                    }
                }
                .addOnFailureListener { e ->
                    _error.value = "Ошибка загрузки данных"
                }
        } else {
            _error.value = "Пользователь не авторизован"
        }
    }

    fun logout() {
        auth.signOut()
    }
}