package com.example.extendogames.ui.activites

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.extendogames.R
import com.example.extendogames.api.services.RetrofitClient
import com.example.extendogames.api.responses.PhoneNumberResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminSupportPhoneActivity : AppCompatActivity() {

    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_support_phone)
        listView = findViewById(R.id.lvPhoneNumbers)
        loadPhoneNumbers()
    }

    private fun loadPhoneNumbers() {
        RetrofitClient.instance.getSupportPhoneNumbers().enqueue(object : Callback<List<PhoneNumberResponse>> {
            override fun onResponse(call: Call<List<PhoneNumberResponse>>, response: Response<List<PhoneNumberResponse>>) {
                if (response.isSuccessful) {
                    val phoneNumbers = response.body()!!
                    val adapter = ArrayAdapter(this@AdminSupportPhoneActivity, android.R.layout.simple_list_item_1, phoneNumbers.map { it.phoneNumber })
                    listView.adapter = adapter
                }
            }

            override fun onFailure(call: Call<List<PhoneNumberResponse>>, t: Throwable) {
            }
        })
    }
}
