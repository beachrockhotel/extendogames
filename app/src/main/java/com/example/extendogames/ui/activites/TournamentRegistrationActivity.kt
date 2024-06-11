package com.example.extendogames.ui.activites

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.extendogames.R
import com.example.extendogames.ui.viewmodels.TournamentRegistrationViewModel

class TournamentRegistrationActivity : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var teamNameEditText: EditText
    private lateinit var membersEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var tournamentNameEditText: EditText
    private lateinit var tournamentDisciplineEditText: EditText

    private val viewModel: TournamentRegistrationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tournament_registration)

        initializeUI()
        setupObservers()

        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()
        }

        intent?.let {
            val tournamentName = it.getStringExtra("TOURNAMENT_NAME") ?: "Default Name"
            val tournamentDiscipline = it.getStringExtra("TOURNAMENT_DISCIPLINE") ?: "Default Discipline"
            tournamentNameEditText.setText(tournamentName)
            tournamentDisciplineEditText.setText(tournamentDiscipline)
        }
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
            viewModel.registerTeam(
                nameEditText.text.toString(),
                emailEditText.text.toString(),
                teamNameEditText.text.toString(),
                membersEditText.text.toString(),
                tournamentNameEditText.text.toString(),
                tournamentDisciplineEditText.text.toString()
            )
        }
    }

    private fun setupObservers() {
        viewModel.userName.observe(this, Observer { name ->
            name?.let { nameEditText.setText(it) }
        })

        viewModel.userEmail.observe(this, Observer { email ->
            email?.let { emailEditText.setText(it) }
        })

        viewModel.message.observe(this, Observer { message ->
            message?.let { Toast.makeText(this, it, Toast.LENGTH_LONG).show() }
        })
    }
}
