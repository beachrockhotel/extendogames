package com.example.extendogames.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.extendogames.api.models.NewsItem
import com.example.extendogames.R

class NewsAdapter(private val newsList: ArrayList<NewsItem>) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = newsList[position]
        holder.title.text = news.title
        holder.text.text = news.text
        if (news.image_url != null) {
            Glide.with(holder.itemView.context)
                .load(news.image_url)
                .into(holder.image)
        }
    }


    override fun getItemCount() = newsList.size

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.newsTitle)
        val text: TextView = itemView.findViewById(R.id.newsText)
        val image: ImageView = itemView.findViewById(R.id.newsImage)
    }
}
