package com.example.extendogames.ui.activites

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.extendogames.R
import com.example.extendogames.ui.viewmodels.AdminSupportPhoneViewModel

class AdminSupportPhoneActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var clearPhoneNumbersButton: Button
    private val viewModel: AdminSupportPhoneViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_support_phone)

        listView = findViewById(R.id.lvPhoneNumbers)
        clearPhoneNumbersButton = findViewById(R.id.clear_support_phone_button)

        observeViewModel()
        setupListViewClickListener()
        setupClearButtonListener()
    }

    private fun observeViewModel() {
        viewModel.phoneNumbers.observe(this, Observer { phoneNumbers ->
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, phoneNumbers.map { "${it.phoneNumber}, ${it.name}, ${it.email}" })
            listView.adapter = adapter
        })

        viewModel.error.observe(this, Observer { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        })

        viewModel.clearSuccess.observe(this, Observer { successMessage ->
            if (successMessage.isNotEmpty()) {
                Toast.makeText(this, successMessage, Toast.LENGTH_LONG).show()
                viewModel.loadPhoneNumbers()  // Reload the list after clearing
            }
        })
    }

    private fun setupListViewClickListener() {
        listView.setOnItemClickListener { parent, view, position, id ->
            val phoneNumber = viewModel.phoneNumbers.value?.get(position)?.phoneNumber
            if (phoneNumber != null) {
                showCallDialog(phoneNumber)
            } else {
                Toast.makeText(this, "Ошибка: номер телефона не найден", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showCallDialog(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        startActivity(intent)
    }

    private fun setupClearButtonListener() {
        clearPhoneNumbersButton.setOnClickListener {
            viewModel.clearPhoneNumbers()
        }
    }
}
