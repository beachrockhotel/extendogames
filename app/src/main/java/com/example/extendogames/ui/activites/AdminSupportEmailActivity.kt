package com.example.extendogames.ui.activites

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.extendogames.R
import com.example.extendogames.ui.viewmodels.AdminSupportEmailViewModel

class AdminSupportEmailActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var clearEmailButton: Button
    private val viewModel: AdminSupportEmailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_support_list)

        listView = findViewById(R.id.listView_support_requests)
        clearEmailButton = findViewById(R.id.clear_support_email_button)

        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()
        }

        observeViewModel()
        setupListViewClickListener()
        setupClearButtonListener()
    }

    private fun observeViewModel() {
        viewModel.supportRequests.observe(this, Observer { supportRequests ->
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, supportRequests.map { "${it.email} - ${it.name} - ${it.question}" })
            listView.adapter = adapter
        })

        viewModel.error.observe(this, Observer { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        })

        viewModel.clearContactInfoSuccess.observe(this, Observer { successMessage ->
            if (successMessage.isNotEmpty()) {
                Toast.makeText(this, successMessage, Toast.LENGTH_LONG).show()
                viewModel.loadSupportRequests()  // Reload the list after clearing
            }
        })
    }

    private fun setupListViewClickListener() {
        listView.setOnItemClickListener { parent, view, position, id ->
            val email = viewModel.supportRequests.value?.get(position)?.email
            if (email != null) {
                sendEmail(email)
            } else {
                Toast.makeText(this, "Ошибка: email не найден", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendEmail(email: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
            putExtra(Intent.EXTRA_SUBJECT, "Support Request")
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "Нет приложения для отправки email", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupClearButtonListener() {
        clearEmailButton.setOnClickListener {
            viewModel.clearContactInfo()
        }
    }
}
