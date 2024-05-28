package com.example.extendogames.managers

import com.example.extendogames.api.models.CartItem
import com.example.extendogames.api.models.MenuItem

object CartManager {
    private val cartItems = mutableListOf<CartItem>()

    fun addToCart(item: MenuItem, quantity: Int) {
        val existingItem = cartItems.find { it.menuItem.id == item.id }
        if (existingItem != null) {
            existingItem.quantity += quantity
        } else {
            cartItems.add(CartItem(item, quantity))
        }
    }

    fun clearCart() {
        cartItems.clear()
    }

    fun getTotalPrice(): Double = cartItems.sumOf { it.menuItem.price * it.quantity }

    fun getCartItems(): MutableList<CartItem> = cartItems
}
