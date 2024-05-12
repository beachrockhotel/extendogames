package com.example.extendogames.ui.activites

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.extendogames.R
import com.example.extendogames.api.services.RetrofitClient
import com.example.extendogames.api.requests.SupportRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SupportEmailActivity : AppCompatActivity() {
    private lateinit var emailInput: EditText
    private lateinit var questionInput: EditText
    private lateinit var sendButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support_email)

        emailInput = findViewById(R.id.email_input)
        questionInput = findViewById(R.id.question_input)
        sendButton = findViewById(R.id.send_button)

        sendButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val question = questionInput.text.toString().trim()

            if (email.isNotEmpty() && question.isNotEmpty()) {
                sendSupportRequest(email, question)
            } else {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendSupportRequest(email: String, question: String) {
        val request = SupportRequest(email, question)
        RetrofitClient.instance.sendSupportRequest(request)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    runOnUiThread {
                        if (response.isSuccessful) {
                            Toast.makeText(applicationContext, "Запрос отправлен", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(applicationContext, "Ошибка отправки запроса: ${response.message()}", Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Ошибка сети: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                }
            })
    }
}
