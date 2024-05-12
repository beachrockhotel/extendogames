package com.example.extendogames.ui.activites

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.extendogames.R
import com.example.extendogames.api.services.RetrofitClient
import com.example.extendogames.api.models.CartItem
import com.example.extendogames.api.models.Order
import com.example.extendogames.api.models.OrderItem
import com.example.extendogames.api.responses.OrderResponse
import com.example.extendogames.managers.CartManager
import com.example.extendogames.ui.adapters.CartAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CartActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var totalPriceTextView: TextView
    private lateinit var itemsCountTextView: TextView
    private lateinit var clearCartButton: Button
    private lateinit var checkoutButton: Button
    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var tableNumberSpinner: Spinner
    private lateinit var adapter: CartAdapter

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        recyclerView = findViewById(R.id.cartRecyclerView)
        totalPriceTextView = findViewById(R.id.total_price)
        itemsCountTextView = findViewById(R.id.items_count)
        clearCartButton = findViewById(R.id.clear_cart_button)
        checkoutButton = findViewById(R.id.checkout_button)
        nameInput = findViewById(R.id.user_name_input)
        emailInput = findViewById(R.id.user_email_input)
        tableNumberSpinner = findViewById(R.id.spinner_table_number)

        ArrayAdapter.createFromResource(this, R.array.table_numbers, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            tableNumberSpinner.adapter = adapter
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CartAdapter(CartManager.getCartItems()) {
            updateTotalPrice()
            updateItemsCount()
        }
        recyclerView.adapter = adapter

        loadUserData()

        clearCartButton.setOnClickListener {
            CartManager.clearCart()
            adapter.updateItems(CartManager.getCartItems())
            updateTotalPrice()
            updateItemsCount()
        }

        checkoutButton.setOnClickListener {
            checkoutOrder()
        }

        updateTotalPrice()
        updateItemsCount()
    }

    private fun loadUserData() {
        val user = auth.currentUser
        if (user != null) {
            firestore.collection("Users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    val name = document.getString("name") ?: "Имя не указано"
                    val email = document.getString("email") ?: "Email не указан"
                    nameInput.setText(name)
                    emailInput.setText(email)
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to load user data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkoutOrder() {
        val name = nameInput.text.toString()
        val email = emailInput.text.toString()
        val tableNumber = tableNumberSpinner.selectedItem.toString()

        if (tableNumber.isBlank()) {
            Toast.makeText(this, "Пожалуйста, выберите номер стола или опцию 'Самовывоз'", Toast.LENGTH_SHORT).show()
            return
        }

        val items = CartManager.getCartItems()
        val total = CartManager.getTotalPrice()
        sendOrderToServer(name, email, tableNumber, items, total)
    }

    private fun sendOrderToServer(name: String, email: String, tableNumber: String, items: List<CartItem>, total: Double) {
        val orderItems = items.map { OrderItem(it.menuItem.name, it.menuItem.price, it.quantity) }
        val orderDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val order = Order(name, email, tableNumber, orderItems, total, orderDate)

        RetrofitClient.instance.createOrder(order).enqueue(object : Callback<OrderResponse> {
            override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    adapter.updateItems(emptyList())  // Очищаем элементы в адаптере
                    updateTotalPrice()
                    updateItemsCount()
                    Toast.makeText(this@CartActivity, "Заказ оформлен", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@CartActivity, "Ошибка оформления заказа: ${response.message()}", Toast.LENGTH_LONG).show()
                }

            }

            override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                Toast.makeText(this@CartActivity, "Ошибка: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun updateTotalPrice() {
        val totalPrice = CartManager.getTotalPrice()
        totalPriceTextView.text = getString(R.string.total_price_format, totalPrice)
    }

    private fun updateItemsCount() {
        val itemsCount = CartManager.getCartItems().size
        itemsCountTextView.text = resources.getQuantityString(R.plurals.items_plural, itemsCount, itemsCount)
    }
}
