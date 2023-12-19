package com.cp.brittany.dixon.activities.home.cart

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.models.CartItem
import com.cp.brittany.dixon.databinding.ItemCartScreenBinding
import com.bumptech.glide.Glide


class CartAdapter(
    val cartItems: MutableList<CartItem>,
    val cartItemInterface: CartItemInterface
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    private lateinit var mContext: Context

    interface CartItemInterface {
        fun onPlus(position: Int)
        fun onMinus(position: Int)
        fun onEdit(position: Int)
        fun onRemove(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context

        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemCartScreenBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_cart_screen,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int = cartItems.size

    inner class ViewHolder(var binding: ItemCartScreenBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun onBind(position: Int) {

            if (!cartItems[position].images.isNullOrEmpty())
                Glide.with(mContext)
                    .load(cartItems[position].images[0].file_path)
                    .into(binding.imgMain)

            binding.tvBrandName.text = cartItems[position].category_name
            binding.tvProductName.text = cartItems[position].product_name
            binding.tvColor.text = "Color: ${cartItems[position].color.color}"
            binding.tvPrice.text = "$${cartItems[position].price.price}"
            binding.tvCount.text = cartItems[position].quantity
            binding.tvSize.text = "Size: ${cartItems[position].size.size}"
            binding.btnPlus.setOnClickListener {
                cartItemInterface.onPlus(adapterPosition)
            }

            binding.btnMinus.setOnClickListener {
                cartItemInterface.onMinus(adapterPosition)
            }

            binding.btnEdit.setOnClickListener {
                cartItemInterface.onEdit(position)
            }

            binding.btnRemove.setOnClickListener {
                cartItemInterface.onRemove(position)
            }
        }
    }
}