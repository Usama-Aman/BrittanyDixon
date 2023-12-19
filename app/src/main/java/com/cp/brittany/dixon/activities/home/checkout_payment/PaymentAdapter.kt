package com.cp.brittany.dixon.activities.home.checkout_payment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.models.CardData
import com.cp.brittany.dixon.databinding.ItemCheckoutPaymentBinding

class PaymentAdapter(var cardlist: MutableList<CardData>, val clickListener: PaymentAdapterInterface) :
    RecyclerView.Adapter<PaymentAdapter.ViewHolder>() {
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemCheckoutPaymentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_checkout_payment,
            parent,
            false
        )
        return ViewHolder(binding, clickListener)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return cardlist.size
    }

    inner class ViewHolder(
        var binding: ItemCheckoutPaymentBinding,
        val itemClickListener: PaymentAdapterInterface
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(position: Int) {
            binding.tvDelete.setOnClickListener {
                itemClickListener.onItemDeleteClicked(position)
            }

            itemView.setOnClickListener {
                clickListener.makeCardDefaultClicked(position)
            }
            binding.tvVisa.text = cardlist[position].card_type

            if (cardlist[position].is_selected) {
                binding.ivCircleTop.setImageResource(R.drawable.ic_circle_filled)
                binding.tvDelete.visibility = View.GONE
            }
            else{
                binding.ivCircleTop.setImageResource(R.drawable.ic_circle)
                binding.tvDelete.visibility = View.VISIBLE
            }


        }


    }

    interface PaymentAdapterInterface {
        fun onItemDeleteClicked(position: Int)
        fun makeCardDefaultClicked(position: Int)
    }
}