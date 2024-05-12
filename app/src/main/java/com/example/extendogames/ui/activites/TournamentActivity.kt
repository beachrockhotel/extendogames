package com.example.extendogames.ui.activites

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.extendogames.R
import com.example.extendogames.api.services.ApiService
import com.example.extendogames.api.models.TournamentItem
import com.example.extendogames.api.responses.TournamentResponse
import com.example.extendogames.ui.adapters.TournamentAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TournamentActivity : AppCompatActivity(), TournamentAdapter.OnItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TournamentAdapter
    private var tournamentList: ArrayList<TournamentItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tournament)

        recyclerView = findViewById(R.id.tournamentRecyclerView)
        adapter = TournamentAdapter(this, tournamentList, this)
        recyclerView.adapter = adapter

        loadTournaments()
    }

    private fun loadTournaments() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val tournamentService = retrofit.create(ApiService::class.java)
        tournamentService.getTournaments().enqueue(object : Callback<TournamentResponse> {
            override fun onResponse(call: Call<TournamentResponse>, response: Response<TournamentResponse>) {
                if (response.isSuccessful) {
                    tournamentList.clear()
                    tournamentList.addAll(response.body()?.tournaments ?: emptyList())
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(applicationContext, "Ошибка: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TournamentResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Не удалось загрузить турниры: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onItemClick(position: Int) {
        val selectedTournament = tournamentList[position]
        val intent = Intent(this, TournamentRegistrationActivity::class.java).apply {
            putExtra("TOURNAMENT_NAME", selectedTournament.title)
            putExtra("TOURNAMENT_DISCIPLINE", selectedTournament.discipline)
        }
        startActivity(intent)
    }
}
