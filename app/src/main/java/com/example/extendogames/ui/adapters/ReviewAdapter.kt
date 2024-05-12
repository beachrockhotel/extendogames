package com.example.extendogames.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.extendogames.R
import com.example.extendogames.api.models.Review

class ReviewAdapter(private var reviews: MutableList<Review> = mutableListOf()) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userEmail: TextView = view.findViewById(R.id.user_email_view)
        val reviewText: TextView = view.findViewById(R.id.review_text)
        val reviewRating: TextView = view.findViewById(R.id.review_rating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.review_item, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.userEmail.text = review.userEmail
        holder.reviewText.text = review.text
        holder.reviewRating.text = "Рейтинг: ${review.rating}"
    }

    override fun getItemCount() = reviews.size

    fun addReview(review: Review) {
        reviews.add(0, review)
        notifyItemInserted(0)
    }

    fun setReviews(newReviews: List<Review>) {
        this.reviews = newReviews.toMutableList()
        notifyDataSetChanged()
    }
}
