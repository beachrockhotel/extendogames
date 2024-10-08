package com.example.extendogames.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.extendogames.R
import com.example.extendogames.api.models.CartItem

class CartAdapter(
    private var items: List<CartItem>,
    private val onUpdate: () -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(view, onUpdate)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<CartItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class CartViewHolder(
        itemView: View,
        private val onUpdate: () -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.ivCartItem)
        private val nameTextView: TextView = itemView.findViewById(R.id.tvCartItemName)
        private val priceTextView: TextView = itemView.findViewById(R.id.tvCartItemPrice)
        private val quantityTextView: TextView = itemView.findViewById(R.id.tvCartItemQuantity)
        private val decreaseButton: Button = itemView.findViewById(R.id.btnDecrease)
        private val increaseButton: Button = itemView.findViewById(R.id.btnIncrease)
        private val totalPriceTextView: TextView = itemView.findViewById(R.id.tvTotalPrice)

        fun bind(cartItem: CartItem) {
            nameTextView.text = cartItem.menuItem.name
            priceTextView.text = "${cartItem.menuItem.price} руб/шт"
            quantityTextView.text = "${cartItem.quantity} шт"

            val totalPrice = cartItem.menuItem.price * cartItem.quantity
            totalPriceTextView.text = "$totalPrice руб"

            Glide.with(itemView.context)
                .load(cartItem.menuItem.image_url)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_background)
                .into(imageView)

            decreaseButton.setOnClickListener {
                if (cartItem.quantity > 1) {
                    cartItem.quantity--
                    notifyItemChanged(adapterPosition)
                } else {
                    (items as MutableList).removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                }
                onUpdate()
            }

            increaseButton.setOnClickListener {
                cartItem.quantity++
                notifyItemChanged(adapterPosition)
                onUpdate()
            }
        }
    }
}
