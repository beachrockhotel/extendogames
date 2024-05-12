package com.example.extendogames.ui.activites

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log
import com.example.extendogames.R
import com.example.extendogames.api.services.RetrofitClient
import com.example.extendogames.api.requests.SupportRequest

class AdminSupportListActivity : AppCompatActivity() {
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_support_list)

        listView = findViewById(R.id.listView_support_requests)
        loadSupportRequests()
    }

    private fun loadSupportRequests() {
        RetrofitClient.instance.getSupportRequests().enqueue(object : Callback<List<SupportRequest>> {
            override fun onResponse(call: Call<List<SupportRequest>>, response: Response<List<SupportRequest>>) {
                if (response.isSuccessful && response.body() != null) {
                    val supportRequests = response.body()!!
                    val adapter = ArrayAdapter(this@AdminSupportListActivity, android.R.layout.simple_list_item_1, supportRequests.map { it.email + " - " + it.question })
                    listView.adapter = adapter
                } else {
                    Log.e("API_ERROR", "Error: ${response.code()} ${response.message()}")
                    Toast.makeText(applicationContext, "Ошибка получения данных: ${response.code()} ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<SupportRequest>>, t: Throwable) {
                Log.e("NETWORK_ERROR", "Failure: ${t.message}")
                Toast.makeText(applicationContext, "Ошибка сети: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
