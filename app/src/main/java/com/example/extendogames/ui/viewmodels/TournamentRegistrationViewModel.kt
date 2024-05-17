package com.example.extendogames.ui.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.extendogames.api.requests.TeamRegistrationRequest
import com.example.extendogames.api.responses.RegistrationResponse
import com.example.extendogames.api.services.ApiService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TournamentRegistrationViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> get() = _userName

    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> get() = _userEmail

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    private val apiService: ApiService

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder().addInterceptor(logging).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        loadUserData()
    }

    private fun loadUserData() {
        val user = auth.currentUser
        if (user != null) {
            firestore.collection("Users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    _userName.value = document.getString("name")
                    _userEmail.value = user.email
                }
                .addOnFailureListener {
                    _message.value = "Не удалось загрузить пользовательские данные"
                }
        }
    }

    fun registerTeam(name: String, email: String, teamName: String, members: String, tournamentName: String, discipline: String) {
        if (name.isBlank() || email.isBlank() || teamName.isBlank() || members.isBlank() || tournamentName.isBlank() || discipline.isBlank()) {
            _message.value = "Пожалуйста, заполните все поля правильно"
            return
        }

        val teamRegistrationRequest = TeamRegistrationRequest(
            name = name,
            email = email,
            teamName = teamName,
            members = members,
            tournamentName = tournamentName,
            discipline = discipline
        )

        apiService.registerTeam(teamRegistrationRequest).enqueue(object : Callback<RegistrationResponse> {
            override fun onResponse(call: Call<RegistrationResponse>, response: Response<RegistrationResponse>) {
                if (response.isSuccessful) {
                    _message.value = "Команда успешно зарегистрирована!"
                } else {
                    _message.value = "Регистрация не удалась: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<RegistrationResponse>, t: Throwable) {
                _message.value = "Ошибка сети: ${t.message}"
            }
        })
    }
}
