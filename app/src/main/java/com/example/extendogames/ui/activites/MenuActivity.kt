package com.example.extendogames.ui.activites

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.extendogames.R
import com.example.extendogames.api.services.RetrofitClient
import com.example.extendogames.api.models.MenuItem
import com.example.extendogames.api.models.MenuResponse
import com.example.extendogames.managers.CartManager
import com.example.extendogames.ui.adapters.MenuAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MenuAdapter
    private lateinit var viewCartButton: Button
    private lateinit var searchView: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        recyclerView = findViewById(R.id.recycler_view_menu)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        searchView = findViewById(R.id.search_view)
        viewCartButton = findViewById(R.id.view_cart_button)

        adapter = MenuAdapter(emptyList()) { menuItem, quantity ->
            addToCart(menuItem, quantity)
        }
        recyclerView.adapter = adapter

        loadProducts()

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

    private fun loadProducts() {
        RetrofitClient.instance.getMenuItems().enqueue(object : Callback<MenuResponse> {
            override fun onResponse(call: Call<MenuResponse>, response: Response<MenuResponse>) {
                if (response.isSuccessful) {
                    adapter = MenuAdapter(response.body()?.menu ?: emptyList()) { menuItem, quantity ->
                        addToCart(menuItem, quantity)
                    }
                    recyclerView.adapter = adapter
                }
            }

            override fun onFailure(call: Call<MenuResponse>, t: Throwable) {
            }
        })
    }

    private fun addToCart(menuItem: MenuItem, quantity: Int) {
        CartManager.addToCart(menuItem, quantity)
    }
}
