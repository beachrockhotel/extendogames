package com.example.extendogames.ui.activites

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.extendogames.R
import com.example.extendogames.ui.viewmodels.ProfileViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.view.MenuItem

class ProfileActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var profileImage: ImageView
    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var userBalance: TextView
    private lateinit var editProfileButton: Button
    private lateinit var accountButton1: Button
    private lateinit var bottomNavigationView: BottomNavigationView

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initializeUI()

        viewModel.userName.observe(this, Observer { name ->
            userName.text = name
        })

        viewModel.userEmail.observe(this, Observer { email ->
            userEmail.text = email
        })

        viewModel.profileImageUrl.observe(this, Observer { url ->
            if (url != null) {
                Glide.with(this).load(url).into(profileImage)
            } else {
                profileImage.setImageResource(R.drawable.game_station_icon)
            }
        })

        viewModel.balance.observe(this, Observer { balance ->
            userBalance.text = "Баланс: $balance руб."
        })

        viewModel.error.observe(this, Observer { error ->
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        })

        val logoutButton = findViewById<Button>(R.id.logout_button)
        logoutButton.setOnClickListener {
            viewModel.logout()
            val intent = Intent(this, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
        bottomNavigationView.selectedItemId = R.id.navigation_profile
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadProfileFromFirestore()
    }

    private fun initializeUI() {
        profileImage = findViewById(R.id.profile_image)
        userName = findViewById(R.id.user_name)
        userEmail = findViewById(R.id.user_email)
        userBalance = findViewById(R.id.user_balance)
        editProfileButton = findViewById(R.id.edit_profile_button)
        accountButton1 = findViewById(R.id.AccountButton_1)

        val backButton = findViewById<ImageView>(R.id.back_arrow)
        backButton.setOnClickListener { finish() }

        accountButton1.setOnClickListener {
            val intent = Intent(this, ProfileEditActivity::class.java)
            startActivityForResult(intent, PROFILE_EDIT_REQUEST_CODE)
        }

        val historyButton = findViewById<Button>(R.id.reservation_history_button)
        historyButton.setOnClickListener {
            val intent = Intent(this, ReservationHistoryActivity::class.java)
            startActivity(intent)
        }

        val questionButton = findViewById<Button>(R.id.question_button)
        questionButton.setOnClickListener {
            val intent = Intent(this, QuestionActivity::class.java)
            startActivity(intent)
        }

        val reviewButton = findViewById<Button>(R.id.review_button)
        reviewButton.setOnClickListener {
            val intent = Intent(this, ReviewActivity::class.java)
            startActivity(intent)
        }

        val contactsButton = findViewById<Button>(R.id.contacts_button)
        contactsButton.setOnClickListener {
            val intent = Intent(this, ContactsActivity::class.java)
            startActivity(intent)
        }

        val orderButton = findViewById<Button>(R.id.reservation_order_button)
        orderButton.setOnClickListener {
            val intent = Intent(this, OrderHistoryActivity::class.java)
            startActivity(intent)
        }

        val supportButton = findViewById<Button>(R.id.support_button)
        supportButton.setOnClickListener {
            val intent = Intent(this, SupportActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PROFILE_EDIT_REQUEST_CODE && resultCode == RESULT_OK) {
            viewModel.loadProfileFromFirestore()
        }
    }

    companion object {
        const val PROFILE_EDIT_REQUEST_CODE = 1
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
                val intent = Intent(this, TournamentActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.navigation_profile -> {
                return true
            }
        }
        return false
    }
}
