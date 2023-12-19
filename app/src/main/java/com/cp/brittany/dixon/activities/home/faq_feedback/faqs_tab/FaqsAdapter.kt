package com.cp.brittany.dixon.activities.home.faq_feedback.faqs_tab

import android.content.Context
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.models.FaqsData
import com.cp.brittany.dixon.databinding.ItemFaqsBinding

class FaqsAdapter() :
    RecyclerView.Adapter<FaqsAdapter.ViewHolder>() {
    private var faqs: MutableList<FaqsData> = ArrayList()
    lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        val binding: ItemFaqsBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_faqs,
            parent,
            false
        )
        return ViewHolder(this, context, binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return faqs.size
    }
    fun setFaqs(list: MutableList<FaqsData>){
        faqs.clear()
        faqs.addAll(list)
    }
    inner class ViewHolder(
        var adapter: FaqsAdapter,
        var context: Context,
        var binding: ItemFaqsBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(position: Int) {
            binding.tvTitle.text = faqs[position].question
            binding.tvAnswer.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(faqs[position].answer, Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml(faqs[position].answer)
            }
            binding.tvTitle.setOnClickListener {
                binding.expandLayout.toggle()

                if (binding.expandLayout.isExpanded) {
//                    binding.ivPlus.scaleX = -1f
                    binding.ivPlus.setImageResource(R.drawable.ic_cross_small)

                } else {
//                    binding.ivPlus.scaleY = 1f
                    binding.ivPlus.setImageResource(R.drawable.ic_plus)

                }
            }
        }
    }
}