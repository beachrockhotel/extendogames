package com.example.extendogames.managers

import com.example.extendogames.api.models.CartItem
import com.example.extendogames.api.models.MenuItem

object CartManager {
    private val cartItems = mutableListOf<CartItem>()

    fun addToCart(item: MenuItem) {
        addToCart(item, 1)
    }

    fun addToCart(item: MenuItem, quantity: Int) {
        val existingItem = cartItems.find { it.menuItem.id == item.id }
        if (existingItem != null) {
            existingItem.quantity += quantity
        } else {
            cartItems.add(CartItem(item, quantity))
        }
    }

    fun decreaseQuantity(item: MenuItem) {
        val existingItem = cartItems.find { it.menuItem.id == item.id }
        if (existingItem != null) {
            if (existingItem.quantity > 1) {
                existingItem.quantity--
            } else {
                cartItems.remove(existingItem)
            }
        }
    }

    fun removeFromCart(item: MenuItem) {
        val index = cartItems.indexOfFirst { it.menuItem.id == item.id }
        if (index != -1) {
            cartItems.removeAt(index)
        }
    }

    fun clearCart() {
        cartItems.clear()
    }

    fun getTotalPrice(): Double = cartItems.sumOf { it.menuItem.price * it.quantity }

    fun getCartItems(): MutableList<CartItem> = cartItems

    fun getQuantity(item: MenuItem): Int {
        val cartItem = cartItems.find { it.menuItem.id == item.id }
        return cartItem?.quantity ?: 0
    }
}
