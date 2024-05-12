package com.example.extendogames.ui.activites

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.extendogames.R
import com.example.extendogames.api.services.RetrofitClient
import com.example.extendogames.api.models.Review
import com.example.extendogames.ui.adapters.ReviewAdapter
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReviewActivity : AppCompatActivity() {
    private val apiService by lazy { RetrofitClient.instance }
    private lateinit var adapter: ReviewAdapter
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private lateinit var reviewRatingSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        reviewRatingSpinner = findViewById(R.id.review_rating_spinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.rating_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            reviewRatingSpinner.adapter = adapter
        }

        initRecyclerView()
        setupReviewSubmission()
    }

    private fun initRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.review_recycler_view)
        adapter = ReviewAdapter()
        recyclerView.adapter = adapter

        apiService.getReviews().enqueue(object : Callback<List<Review>> {
            override fun onResponse(call: Call<List<Review>>, response: Response<List<Review>>) {
                if (response.isSuccessful) {
                    response.body()?.let { adapter.setReviews(it) }
                } else {
                    Toast.makeText(applicationContext, "Ошибка загрузки отзывов: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Review>>, t: Throwable) {
                Toast.makeText(applicationContext, "Ошибка подключения: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun setupReviewSubmission() {
        val reviewText: EditText = findViewById(R.id.review_input)
        val submitButton: Button = findViewById(R.id.submit_review_button)

        submitButton.setOnClickListener {
            val text = reviewText.text.toString().trim()
            val rating = reviewRatingSpinner.selectedItem.toString().toInt()
            if (text.isBlank()) {
                Toast.makeText(this, "Введите корректный отзыв.", Toast.LENGTH_SHORT).show()
            } else {
                val userEmail = auth.currentUser?.email ?: "email@notavailable.com"
                val review = Review(userEmail = userEmail, text = text, rating = rating)
                postReview(review)
            }
        }
    }

    private fun postReview(review: Review) {
        apiService.postReview(review).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(applicationContext, "Отзыв успешно добавлен", Toast.LENGTH_SHORT).show()
                    adapter.addReview(review)
                } else {
                    Toast.makeText(applicationContext, "Ошибка при добавлении отзыва: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(applicationContext, "Ошибка подключения: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
