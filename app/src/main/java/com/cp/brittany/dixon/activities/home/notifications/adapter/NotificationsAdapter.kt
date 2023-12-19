package com.cp.brittany.dixon.activities.home.notifications.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.home_models.NotificationsList
import com.cp.brittany.dixon.databinding.ItemNotificationsBinding
import com.bumptech.glide.Glide

class NotificationsAdapter(var context: Context, var list: MutableList<NotificationsList>, val mListener: NotificationInterface) :
    RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemNotificationsBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_notifications,
            parent,
            false
        )
        return ViewHolder(binding,mListener)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(item: MutableList<NotificationsList>) {
        this.list = item
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(
        var binding: ItemNotificationsBinding,
        val listener: NotificationInterface
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(position: Int) {
            val model = list[position]
            binding.tvTitle.text = model.title
            binding.tvDescription.text = model.notification
            if (model.notification_data !== null)
                Glide.with(context).load(model.notification_data?.image_path)
                    .into(binding.ivImage)
            itemView.setOnClickListener {
                listener.onItemClicked(position)
                //binding.ivDot.visibility = View.GONE
            }
            if(model.is_read == 1){
                binding.ivDot.visibility = View.GONE
            }
            else{
                binding.ivDot.visibility = View.VISIBLE
            }

        }
    }
    interface NotificationInterface{
        fun onItemClicked(position: Int)
    }
}