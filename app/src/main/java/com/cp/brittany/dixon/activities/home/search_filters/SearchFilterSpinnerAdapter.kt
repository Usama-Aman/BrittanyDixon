package com.cp.brittany.dixon.activities.home.search_filters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.Nullable
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.search.food_tab.FilterCategory

class SearchFilterSpinnerAdapter(
    private val contex: Context,
    private val listClass: List<FilterCategory>
) : ArrayAdapter<FilterCategory>(contex, 0, listClass) {

    override fun getView(position: Int, @Nullable convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)!!
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View? {
        return initView(position, convertView, parent)
    }

    private fun initView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val vv: View? = LayoutInflater.from(contex).inflate(
            R.layout.item_filters_spinner, parent, false
        )

        val textViewName = vv?.findViewById<TextView>(R.id.tvSpinner)
        textViewName?.text = listClass[position].name
        textViewName?.textSize = 16F
        val view = vv?.findViewById<View>(R.id.viewSp)

        if (position == listClass.size - 1) {
            view?.visibility = View.GONE
        } else {
            view?.visibility = View.VISIBLE
        }

        return vv
    }
}