package com.example.extendogames.ui.activites

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.extendogames.R
import com.example.extendogames.api.models.TournamentItem
import com.example.extendogames.ui.adapters.TournamentAdapter
import com.example.extendogames.ui.viewmodels.TournamentViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class TournamentActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener, TournamentAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TournamentAdapter
    private lateinit var bottomNavigationView: BottomNavigationView
    private val viewModel: TournamentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tournament)

        recyclerView = findViewById(R.id.tournamentRecyclerView)
        adapter = TournamentAdapter(this, arrayListOf(), this)
        recyclerView.adapter = adapter

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
        bottomNavigationView.selectedItemId = R.id.navigation_tournaments

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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.navigation_news -> {
                val intent = Intent(this, NewsActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.navigation_menu -> {
                val intent = Intent(this, MenuActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.navigation_tournaments -> {
                return true
            }
            R.id.navigation_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return false
    }
}
