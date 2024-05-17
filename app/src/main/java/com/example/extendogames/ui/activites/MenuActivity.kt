package com.example.extendogames.ui.activites

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.extendogames.R
import com.example.extendogames.api.services.RetrofitClient
import com.example.extendogames.ui.adapters.MenuAdapter
import com.example.extendogames.ui.factory.MenuViewModelFactory
import com.example.extendogames.ui.viewmodels.MenuViewModel

class MenuActivity : AppCompatActivity() {
    private val viewModel: MenuViewModel by viewModels {
        MenuViewModelFactory(RetrofitClient.instance)
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MenuAdapter
    private lateinit var viewCartButton: Button
    private lateinit var searchView: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        recyclerView = findViewById(R.id.recycler_view_menu)
        searchView = findViewById(R.id.search_view)
        viewCartButton = findViewById(R.id.view_cart_button)

        recyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = MenuAdapter(emptyList()) { menuItem, quantity ->
            viewModel.addToCart(menuItem, quantity)
        }
        recyclerView.adapter = adapter

        viewModel.menuItems.observe(this) { menuItems ->
            adapter.updateItems(menuItems)
        }

        viewModel.loadMenuItems()

        searchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        viewCartButton.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }
    }
}
