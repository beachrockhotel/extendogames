package com.example.extendogames.ui.activites

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.extendogames.R
import com.example.extendogames.ui.viewmodels.AdminSupportListViewModel

class AdminSupportListActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private val viewModel: AdminSupportListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_support_list)

        listView = findViewById(R.id.listView_support_requests)

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.supportRequests.observe(this, Observer { supportRequests ->
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, supportRequests.map { it.email + " - " + it.question })
            listView.adapter = adapter
        })

        viewModel.error.observe(this, Observer { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        })
    }
}
