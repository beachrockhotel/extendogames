package com.example.extendogames.ui.activites

import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.extendogames.R
import com.example.extendogames.api.models.Order
import com.example.extendogames.api.models.OrderItem
import com.example.extendogames.api.services.RetrofitClient
import com.example.extendogames.ui.adapters.CartAdapter
import com.example.extendogames.ui.factory.CartViewModelFactory
import com.example.extendogames.ui.viewmodels.CartViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot

class CartActivity : AppCompatActivity() {
    private val viewModel: CartViewModel by viewModels { CartViewModelFactory(RetrofitClient.instance) }
    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        setupUI()
        loadUserData()
    }

    private fun setupUI() {
        val recyclerView: RecyclerView = findViewById(R.id.cartRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val totalPriceTextView: TextView = findViewById(R.id.total_price)
        val itemsCountTextView: TextView = findViewById(R.id.items_count)
        val clearCartButton: Button = findViewById(R.id.clear_cart_button)
        val checkoutButton: Button = findViewById(R.id.checkout_button)
        nameInput = findViewById(R.id.user_name_input)
        emailInput = findViewById(R.id.user_email_input)

        val adapter = CartAdapter(emptyList()) {
            viewModel.updateCart()
        }
        recyclerView.adapter = adapter

        viewModel.cartItems.observe(this) { items ->
            adapter.updateItems(items)
        }
        viewModel.totalPrice.observe(this) { total ->
            totalPriceTextView.text = getString(R.string.total_price_format, total)
        }
        viewModel.itemsCount.observe(this) { count ->
            itemsCountTextView.text = resources.getQuantityString(R.plurals.items_plural, count, count)
        }

        clearCartButton.setOnClickListener {
            viewModel.clearCart()
        }

        checkoutButton.setOnClickListener {
            checkUserBalanceAndCheckout()
        }
    }

    private fun loadUserData() {
        val user = auth.currentUser
        user?.let {
            firestore.collection("Users").document(it.uid).get()
                .addOnSuccessListener { document ->
                    nameInput.setText(document.getString("name"))
                    emailInput.setText(document.getString("email"))
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Ошибка загрузки данных пользователя: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun checkUserBalanceAndCheckout() {
        val user = auth.currentUser
        user?.let {
            val userDoc = firestore.collection("Users").document(it.uid)
            userDoc.get().addOnSuccessListener { document ->
                val balance = document.getDouble("balance") ?: 0.0
                val total = viewModel.totalPrice.value ?: 0.0

                if (balance >= total) {
                    checkout(userDoc, balance - total)
                } else {
                    Toast.makeText(this, "Недостаточно средств на счету", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Ошибка проверки баланса: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkout(userDoc: DocumentReference, newBalance: Double) {
        val name = nameInput.text.toString()
        val email = emailInput.text.toString()
        val tableNumber = findViewById<Spinner>(R.id.spinner_table_number).selectedItem.toString()
        val items = viewModel.cartItems.value ?: listOf()
        val total = viewModel.totalPrice.value ?: 0.0
        val orderDate = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)

        val order = Order(
            user_name = name,
            user_email = email,
            table_number = tableNumber,
            items = items.map { OrderItem(it.menuItem.name, it.menuItem.price, it.quantity) },
            total_price = total,
            order_date = orderDate
        )

        userDoc.update("balance", newBalance).addOnSuccessListener {
            viewModel.createOrder(order, {
                Toast.makeText(this, "Заказ успешно оформлен!", Toast.LENGTH_SHORT).show()
            }, { error ->
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            })
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Ошибка обновления баланса: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
