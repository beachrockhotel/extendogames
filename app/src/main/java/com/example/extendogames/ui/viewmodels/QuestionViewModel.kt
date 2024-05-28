package com.example.extendogames.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.extendogames.api.models.Answer
import com.example.extendogames.api.models.Question
import com.example.extendogames.api.services.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuestionViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = RetrofitClient.instance
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val _questions = MutableLiveData<List<Question>>()
    val questions: LiveData<List<Question>> get() = _questions

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    init {
        loadQuestions()
    }

    private fun loadQuestions() {
        apiService.getQuestions().enqueue(object : Callback<List<Question>> {
            override fun onResponse(call: Call<List<Question>>, response: Response<List<Question>>) {
                if (response.isSuccessful) {
                    _questions.value = response.body()
                } else {
                    _message.value = "Ошибка загрузки вопросов: ${response.errorBody()?.string()}"
                }
            }

            override fun onFailure(call: Call<List<Question>>, t: Throwable) {
                _message.value = "Ошибка подключения: ${t.localizedMessage}"
            }
        })
    }

    fun postQuestion(userEmail: String, text: String) {
        val question = Question(id = 0, userEmail = userEmail, text = text, answers = emptyList())
        apiService.postQuestion(question).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    _message.value = "Сообщение отправлено"
                    loadQuestions()
                } else {
                    _message.value = "Ошибка при добавлении вопроса: ${response.errorBody()?.string()}"
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                _message.value = "Ошибка подключения: ${t.localizedMessage}"
            }
        })
    }

    fun postAnswer(questionId: Int, answerText: String) {
        val userEmail = getCurrentUserEmail()
        apiService.postAnswer(questionId, Answer(text = answerText, userEmail = userEmail)).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    _message.value = "Ответ успешно отправлен"
                    loadQuestions()
                } else {
                    _message.value = "Ошибка при добавлении ответа: ${response.errorBody()?.string()}"
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                _message.value = "Ошибка подключения: ${t.localizedMessage}"
            }
        })
    }

    fun getCurrentUserEmail(): String {
        return auth.currentUser?.email ?: "email@notavailable.com"
    }
}
