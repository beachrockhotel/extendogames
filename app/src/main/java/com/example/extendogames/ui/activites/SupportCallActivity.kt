package com.example.extendogames.ui.activites

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.extendogames.R
import com.example.extendogames.api.services.RetrofitClient
import com.example.extendogames.api.requests.NotificationRequest
import com.example.extendogames.api.responses.NotificationResponse
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SupportCallActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support_call)

        val etTableNumber = findViewById<EditText>(R.id.etTableNumber)
        val btnCallSupport = findViewById<Button>(R.id.btnCallSupport)

        btnCallSupport.setOnClickListener {
            val tableNumber = etTableNumber.text.toString().trim()
            if (tableNumber.isEmpty()) {
                Toast.makeText(this, "Please enter a table number", Toast.LENGTH_SHORT).show()
            } else {
                sendHelpRequest(tableNumber)
            }
        }
    }

    fun getSavedFcmToken(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("FCM_PREF", Context.MODE_PRIVATE)
        return sharedPreferences.getString("FCM_TOKEN", null)
    }


    private fun sendHelpRequest(tableNumber: String) {
        Log.d("SupportCallActivity", "Sending help request for table number: $tableNumber")
        FirebaseFirestore.getInstance()
            .collection("Users")
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(this, "No users found", Toast.LENGTH_SHORT).show()
                } else {
                    for (document in documents) {
                        val token = document.getString("fcmToken")
                        if (token != null) {
                            sendNotificationToServer(token, tableNumber)
                        } else {
                            Log.d("SupportCallActivity", "User ${document.id} does not have an fcmToken")
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching user data: $e", Toast.LENGTH_LONG).show()
                Log.e("SupportCallActivity", "Error fetching user data", e)
            }
    }

    private fun sendNotificationToServer(token: String, tableNumber: String) {
        val request = NotificationRequest(adminToken = token, tableNumber = tableNumber)
        RetrofitClient.instance.sendNotification(request)
            .enqueue(object : Callback<NotificationResponse> {
                override fun onResponse(call: Call<NotificationResponse>, response: Response<NotificationResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@SupportCallActivity, "Help request sent for table $tableNumber", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@SupportCallActivity, "Failed to send help request", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                    Toast.makeText(this@SupportCallActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }
}

