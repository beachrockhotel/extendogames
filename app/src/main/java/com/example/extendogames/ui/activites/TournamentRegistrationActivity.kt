package com.example.extendogames.ui.activites

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.extendogames.R
import com.example.extendogames.api.services.ApiService
import com.example.extendogames.api.requests.TeamRegistrationRequest
import com.example.extendogames.api.responses.RegistrationResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TournamentRegistrationActivity : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var teamNameEditText: EditText
    private lateinit var membersEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var tournamentNameEditText: EditText
    private lateinit var tournamentDisciplineEditText: EditText
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tournament_registration)

        initializeUI()
        loadUserData()

        intent?.let {
            val tournamentName = it.getStringExtra("TOURNAMENT_NAME") ?: "Default Name"
            val tournamentDiscipline = it.getStringExtra("TOURNAMENT_DISCIPLINE") ?: "Default Discipline"
            findViewById<EditText>(R.id.tournament_name).setText(tournamentName)
            findViewById<EditText>(R.id.tournament_discipline).setText(tournamentDiscipline)
        }
    }


    private fun fillTournamentDetails() {
        val tournamentName = intent.getStringExtra("TOURNAMENT_NAME") ?: "Default Name"
        val tournamentDiscipline = intent.getStringExtra("TOURNAMENT_DISCIPLINE") ?: "Default Discipline"
        Log.d("TournamentRegistration", "Tournament Name: $tournamentName, Discipline: $tournamentDiscipline")
        tournamentNameEditText.setText(tournamentName)
        tournamentDisciplineEditText.setText(tournamentDiscipline)
    }



    private fun initializeUI() {
        nameEditText = findViewById(R.id.name_tournament_registration)
        emailEditText = findViewById(R.id.email_tournament_reg)
        teamNameEditText = findViewById(R.id.team_name_tournament_registration)
        membersEditText = findViewById(R.id.members_tournament_registration)
        registerButton = findViewById(R.id.register_button)
        tournamentNameEditText = findViewById(R.id.tournament_name)
        tournamentDisciplineEditText = findViewById(R.id.tournament_discipline)

        registerButton.setOnClickListener {
            registerTeam()
        }
    }

    private fun loadUserData() {
        val user = auth.currentUser
        if (user != null) {
            firestore.collection("Users").document(user.uid).get().addOnSuccessListener { document ->
                nameEditText.setText(document.getString("name"))
                emailEditText.setText(user.email)
            }.addOnFailureListener {
                Toast.makeText(this, "Не удалось загрузить пользовательские данные", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerTeam() {
        if (nameEditText.text.isBlank() || emailEditText.text.isBlank() || teamNameEditText.text.isBlank() ||
            membersEditText.text.isBlank() || tournamentNameEditText.text.isBlank() || tournamentDisciplineEditText.text.isBlank()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля правильно", Toast.LENGTH_SHORT).show()
            return
        }


        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        val teamRegistrationRequest = TeamRegistrationRequest(
            name = nameEditText.text.toString(),
            email = emailEditText.text.toString(),
            teamName = teamNameEditText.text.toString(),
            members = membersEditText.text.toString(),
            tournamentName = tournamentNameEditText.text.toString(), // Убедитесь, что этот EditText существует и корректно инициализирован
            discipline = tournamentDisciplineEditText.text.toString()          // Убедитесь, что этот EditText существует и корректно инициализирован
        )

        apiService.registerTeam(teamRegistrationRequest).enqueue(object : Callback<RegistrationResponse> {
            override fun onResponse(call: Call<RegistrationResponse>, response: Response<RegistrationResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(applicationContext, "Команда успешно зарегистрирована!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "Регистрация не удалась: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegistrationResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
