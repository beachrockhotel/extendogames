package com.example.extendogames.ui.activites

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.bumptech.glide.Glide
import com.example.extendogames.R

class ProfileActivity : AppCompatActivity() {

    private lateinit var profileImage: ImageView
    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var editProfileButton: Button
    private lateinit var accountButton1: Button
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initializeUI()
        loadProfileFromFirestore()

        val logoutButton = findViewById<Button>(R.id.logout_button)
        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }


    private fun initializeUI() {
        profileImage = findViewById(R.id.profile_image)
        userName = findViewById(R.id.user_name)
        userEmail = findViewById(R.id.user_email)
        editProfileButton = findViewById(R.id.edit_profile_button)
        accountButton1 = findViewById(R.id.AccountButton_1)

        val backButton = findViewById<ImageView>(R.id.back_arrow)
        backButton.setOnClickListener { finish() }

        accountButton1.setOnClickListener {
            val intent = Intent(this, ProfileEditActivity::class.java)
            startActivity(intent)
        }

        val historyButton = findViewById<Button>(R.id.reservation_history_button)
        historyButton.setOnClickListener {
            val intent = Intent(this, ReservationHistoryActivity::class.java)
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

    private fun loadProfileFromFirestore() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("Users").document(userId).get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val name = document.getString("name") ?: "Имя не указано"
                    val email = auth.currentUser?.email ?: "Email не указан"
                    val photoUrl = auth.currentUser?.photoUrl

                    userName.text = name
                    userEmail.text = email
                    Glide.with(this).load(photoUrl).into(profileImage)
                } else {
                    userName.text = "Пользователь не найден"
                    userEmail.text = "Email не указан"
                }
            }.addOnFailureListener { e ->
                userName.text = "Ошибка загрузки данных"
                userEmail.text = "Email не указан"
                e.printStackTrace()
            }
        }
    }
}
