package com.example.extendogames.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.extendogames.R
import com.example.extendogames.api.models.Answer

class AnswerAdapter(private var answers: List<Answer>) : RecyclerView.Adapter<AnswerAdapter.AnswerViewHolder>() {

    class AnswerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val answerUserEmail: TextView = view.findViewById(R.id.answer_user_email_view)
        val answerText: TextView = view.findViewById(R.id.answer_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.answer_item, parent, false)
        return AnswerViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
        holder.answerUserEmail.text = answers[position].userEmail
        holder.answerText.text = answers[position].text
    }

    override fun getItemCount() = answers.size

    fun setAnswers(newAnswers: List<Answer>) {
        this.answers = newAnswers
        notifyDataSetChanged()
    }
}
