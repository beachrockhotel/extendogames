package com.example.extendogames.ui.activites

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.extendogames.R
import com.example.extendogames.api.models.TournamentItem
import com.example.extendogames.ui.adapters.TournamentAdapter
import com.example.extendogames.ui.viewmodels.TournamentViewModel

class TournamentActivity : AppCompatActivity(), TournamentAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TournamentAdapter
    private val viewModel: TournamentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tournament)

        recyclerView = findViewById(R.id.tournamentRecyclerView)
        adapter = TournamentAdapter(this, arrayListOf(), this)
        recyclerView.adapter = adapter

        setupObservers()
    }

    private fun setupObservers() {
        viewModel.tournaments.observe(this, Observer { tournaments ->
            tournaments?.let {
                adapter.setTournaments(it)
            }
        })

        viewModel.message.observe(this, Observer { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onItemClick(position: Int) {
        val selectedTournament = adapter.getTournamentAt(position)
        val intent = Intent(this, TournamentRegistrationActivity::class.java).apply {
            putExtra("TOURNAMENT_NAME", selectedTournament.title)
            putExtra("TOURNAMENT_DISCIPLINE", selectedTournament.discipline)
        }
        startActivity(intent)
    }
}
