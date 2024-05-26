package com.example.extendogames.ui.activites

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.extendogames.R
import com.example.extendogames.ui.viewmodels.AdminSupportPhoneViewModel

class AdminSupportPhoneActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private val viewModel: AdminSupportPhoneViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_support_phone)

        listView = findViewById(R.id.lvPhoneNumbers)

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.phoneNumbers.observe(this, Observer { phoneNumbers ->
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, phoneNumbers.map { "${it.phoneNumber}, ${it.name}, ${it.email}" })
            listView.adapter = adapter
        })

        viewModel.error.observe(this, Observer { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        })
    }
}
