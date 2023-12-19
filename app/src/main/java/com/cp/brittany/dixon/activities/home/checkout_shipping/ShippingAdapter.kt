package com.cp.brittany.dixon.activities.home.checkout_shipping

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.models.ShippingAddresse
import com.cp.brittany.dixon.databinding.ItemShippingAddressBinding
import com.bumptech.glide.Glide
import com.cp.brittany.dixon.utils.viewGone
import com.cp.brittany.dixon.utils.viewVisible

class ShippingAdapter(var shippingAddresses: MutableList<ShippingAddresse>, val clickListener: ShippingAdapterInterface) :
    RecyclerView.Adapter<ShippingAdapter.ViewHolder>() {

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemShippingAddressBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_shipping_address,
            parent,
            false
        )
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int = shippingAddresses.size

    inner class ViewHolder(var binding: ItemShippingAddressBinding) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(position: Int) {

            if (shippingAddresses[position].is_selected) {
                Glide.with(mContext)
                    .load(R.drawable.ic_circle_filled)
                    .into(binding.ivCircleTop)
                binding.tvEdit.viewGone()
                binding.tvDelete.viewGone()
            } else {
                Glide.with(mContext)
                    .load(R.drawable.ic_circle)
                    .into(binding.ivCircleTop)
                binding.tvEdit.viewVisible()
                binding.tvDelete.viewVisible()
            }

            binding.tvLocation.text = shippingAddresses[position].street_address
            binding.tvPhone.text = shippingAddresses[position].phone_number

            itemView.setOnClickListener {
                clickListener.makeCardDefault(position)
            }
            binding.tvDelete.setOnClickListener {
                clickListener.onItemDeleteClicked(position)
            }
            binding.tvEdit.setOnClickListener {
                clickListener.onItemEditClicked(position)
            }
        }
    }

    interface ShippingAdapterInterface {
        fun onItemEditClicked(position: Int)
        fun onItemDeleteClicked(position: Int)
        fun makeCardDefault(position: Int)
    }
}
