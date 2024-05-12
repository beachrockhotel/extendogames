package com.example.extendogames.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.extendogames.R
import com.example.extendogames.api.models.MenuItem

class MenuAdapter(
    private var fullMenuList: List<MenuItem>,
    private val onAddToCart: (MenuItem, Int) -> Unit
) : RecyclerView.Adapter<MenuAdapter.ProductViewHolder>(), Filterable {

    private var menuList: List<MenuItem> = fullMenuList.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(menuList[position])
    }

    override fun getItemCount(): Int = menuList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = if (constraint.isNullOrEmpty()) {
                    fullMenuList
                } else {
                    fullMenuList.filter { it.name.lowercase().contains(constraint.toString().lowercase().trim()) }
                }

                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                @Suppress("UNCHECKED_CAST")
                menuList = results?.values as List<MenuItem>? ?: listOf()
                notifyDataSetChanged()
            }
        }
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.image_view_product)
        private val nameTextView: TextView = itemView.findViewById(R.id.text_view_name)
        private val priceTextView: TextView = itemView.findViewById(R.id.text_view_price)
        private val addButton: Button = itemView.findViewById(R.id.button_add)

        fun bind(menuItem: MenuItem) {
            nameTextView.text = menuItem.name
            priceTextView.text = "${menuItem.price} руб"

            if (menuItem.image_url.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(menuItem.image_url)
                    .apply(RequestOptions().placeholder(R.drawable.ic_launcher_background).error(R.drawable.ic_launcher_foreground))
                    .into(imageView)
            }

            addButton.setOnClickListener {
                onAddToCart(menuItem, 1)
            }
        }
    }
}
