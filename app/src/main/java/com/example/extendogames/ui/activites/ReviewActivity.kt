package com.example.extendogames.ui.activites

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.extendogames.R
import com.example.extendogames.api.models.Review
import com.example.extendogames.ui.adapters.ReviewAdapter
import com.example.extendogames.ui.viewmodels.ReviewViewModel

class ReviewActivity : AppCompatActivity() {

    private val viewModel: ReviewViewModel by viewModels()
    private lateinit var adapter: ReviewAdapter
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
        setupObservers()
        setupReviewSubmission()
    }

    private fun initRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.review_recycler_view)
        adapter = ReviewAdapter()
        recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.reviews.observe(this, Observer { reviews ->
            reviews?.let { adapter.setReviews(it) }
        })

        viewModel.message.observe(this, Observer { message ->
            message?.let { Toast.makeText(this, it, Toast.LENGTH_LONG).show() }
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
                val userEmail = viewModel.getCurrentUserEmail()
                val review = Review(userEmail = userEmail, text = text, rating = rating)
                viewModel.postReview(review)
            }
        }
    }
}
