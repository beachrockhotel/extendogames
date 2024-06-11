package com.example.extendogames.ui.activites

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.extendogames.R
import com.example.extendogames.ui.adapters.QuestionAdapter
import com.example.extendogames.ui.viewmodels.QuestionViewModel

class QuestionActivity : AppCompatActivity() {

    private val viewModel: QuestionViewModel by viewModels()
    private lateinit var adapter: QuestionAdapter
    private lateinit var questionText: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()
        }

        questionText = findViewById(R.id.question_input)
        submitButton = findViewById(R.id.submit_question_button)

        initRecyclerView()
        setupObservers()
        setupQuestionSubmission()
    }

    private fun initRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.question_recycler_view)
        adapter = QuestionAdapter { questionId, answerText ->
            viewModel.postAnswer(questionId, answerText)
        }
        recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.questions.observe(this, Observer { questions ->
            questions?.let { adapter.setQuestions(it) }
        })

        viewModel.message.observe(this, Observer { message ->
            message?.let { Toast.makeText(this, it, Toast.LENGTH_LONG).show() }
        })
    }

    private fun setupQuestionSubmission() {
        submitButton.setOnClickListener {
            val text = questionText.text.toString().trim()
            if (text.isBlank()) {
                Toast.makeText(this, "Введите корректный вопрос.", Toast.LENGTH_SHORT).show()
            } else {
                val userEmail = viewModel.getCurrentUserEmail()
                viewModel.postQuestion(userEmail, text)
            }
        }
    }
}
