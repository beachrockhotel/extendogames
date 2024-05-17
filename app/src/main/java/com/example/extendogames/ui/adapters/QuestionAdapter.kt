package com.example.extendogames.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.extendogames.R
import com.example.extendogames.api.models.Question

class QuestionAdapter(private val onAnswerSubmit: (Int, String) -> Unit) : RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder>() {

    private var questions: MutableList<Question> = mutableListOf()

    class QuestionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userEmail: TextView = view.findViewById(R.id.question_user_email_view)
        val questionText: TextView = view.findViewById(R.id.question_text)
        val answerInput: EditText = view.findViewById(R.id.answer_input)
        val submitAnswerButton: Button = view.findViewById(R.id.submit_answer_button)
        val answersRecyclerView: RecyclerView = view.findViewById(R.id.answers_recycler_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.question_item, parent, false)
        return QuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val question = questions[position]
        holder.userEmail.text = question.userEmail
        holder.questionText.text = question.text

        val answerAdapter = AnswerAdapter(question.answers)
        holder.answersRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.answersRecyclerView.adapter = answerAdapter

        holder.submitAnswerButton.setOnClickListener {
            val answerText = holder.answerInput.text.toString().trim()
            if (answerText.isNotBlank()) {
                onAnswerSubmit(question.id, answerText)
                holder.answerInput.setText("")
            }
        }
    }

    override fun getItemCount() = questions.size

    fun setQuestions(newQuestions: List<Question>) {
        this.questions = newQuestions.toMutableList()
        notifyDataSetChanged()
    }
}
