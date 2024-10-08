package com.example.extendogames.ui.activites

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.extendogames.R
import com.example.extendogames.api.models.NewsItem
import com.example.extendogames.ui.adapters.NewsAdapter
import com.example.extendogames.ui.viewmodels.NewsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class NewsActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private val viewModel: NewsViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NewsAdapter
    private lateinit var bottomNavigationView: BottomNavigationView
    private var newsList: ArrayList<NewsItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        recyclerView = findViewById(R.id.newsRecyclerView)
        adapter = NewsAdapter(newsList)
        recyclerView.adapter = adapter

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
        bottomNavigationView.selectedItemId = R.id.navigation_news

        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()
        }

        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.navigation_news
    }

    private fun observeViewModel() {
        viewModel.newsItems.observe(this, Observer { newsItems ->
            newsList.clear()
            newsList.addAll(newsItems)
            adapter.notifyDataSetChanged()
        })

        viewModel.errorMessage.observe(this, Observer { errorMessage ->
            Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
        })
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val userPrivileges = intent.getBooleanExtra("userPrivileges", false)
        when (item.itemId) {
            R.id.navigation_home -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("userPrivileges", userPrivileges)
                startActivity(intent)
                return true
            }
            R.id.navigation_news -> {
                return true
            }
            R.id.navigation_menu -> {
                val intent = Intent(this, MenuActivity::class.java)
                intent.putExtra("userPrivileges", userPrivileges)
                startActivity(intent)
                return true
            }
            R.id.navigation_tournaments -> {
                val intent = Intent(this, TournamentActivity::class.java)
                intent.putExtra("userPrivileges", userPrivileges)
                startActivity(intent)
                return true
            }
            R.id.navigation_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra("userPrivileges", userPrivileges)
                startActivity(intent)
                return true
            }
        }
        return false
    }
}
