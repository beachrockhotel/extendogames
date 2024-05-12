package com.example.extendogames.ui.activites

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.extendogames.R
import com.example.extendogames.api.services.RetrofitClient
import com.example.extendogames.api.requests.PhoneNumberRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SupportPhoneActivity : AppCompatActivity() {
    private lateinit var phoneNumberInput: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support_phone)

        phoneNumberInput = findViewById(R.id.phone_input)
        submitButton = findViewById(R.id.submit_button)

        submitButton.setOnClickListener {
            val phoneNumber = phoneNumberInput.text.toString().trim()
            if (phoneNumber.isNotEmpty()) {
                sendPhoneNumber(phoneNumber)
            } else {
                Toast.makeText(this, "Пожалуйста, введите номер телефона", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendPhoneNumber(phoneNumber: String) {
        RetrofitClient.instance.sendPhoneNumber(PhoneNumberRequest(phoneNumber))
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(applicationContext, "Номер телефона успешно отправлен", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(applicationContext, "Не удалось отправить номер телефона", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(applicationContext, "Ошибка: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }
}
