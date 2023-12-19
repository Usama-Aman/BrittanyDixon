package com.cp.brittany.dixon.activities.home.search_filters

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.search.food_tab.FoodFilterData
import com.cp.brittany.dixon.activities.home.search.food_tab.FoodFilterResult
import com.cp.brittany.dixon.activities.home.search_filters.adapters.TagAdapters
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivityFoodFilterBinding
import com.cp.brittany.dixon.utils.AppUtils

class FoodFilterActivity : BaseActivity() {
    private lateinit var binding: ActivityFoodFilterBinding
    private var categoriesAdapter: SearchFilterSpinnerAdapter? = null
    private var foodTypeAdapter: TagAdapters? = null
    private var weightAdapter: TagAdapters? = null

    private var foodFilters: FoodFilterData? = null
    private var durationIntervals: MutableList<Int> = ArrayList()
    private var isFilterClear = false

    companion object {
        var filterData: FoodFilterResult? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()

        initViews()
        initListeners()
        //initAdapters()
    }

    private fun initViews() {
        filterData = intent?.getParcelableExtra("filterData")
        foodFilters = intent?.getParcelableExtra("filters")
        if (foodFilters != null) {

            for (i in foodFilters?.categories!!.indices) {
                if (foodFilters?.categories!![i].id == filterData?.categoryId)
                    binding.spCategories.setSelection(i)
            }
            initAdapters()


            var interval = foodFilters?.max_calories?.toDouble()?.toInt()!! / 5
            if (interval < 1) {
                interval = 1
            }
            var count = foodFilters?.max_calories?.toDouble()?.toInt()!!
            if (count < 5) {
                count = 5
            }
            //durationIntervals.add(1)
            outer@ for (i in 0..count step interval)
                if (i + interval <= count)
                    durationIntervals.add(i + interval)
                else
                    break@outer
            binding.seekBarCalories.valueFrom = durationIntervals[0].toFloat()
            binding.seekBarCalories.valueTo = count.toFloat()
            binding.seekBarCalories.stepSize = interval.toFloat()
            binding.tv10.text = "${durationIntervals[0]}"
            binding.tv15.text = "${durationIntervals[1]}"
            binding.tv20.text = "${durationIntervals[2]}"
            binding.tv25.text = "${durationIntervals[3]}"
            binding.tv30.text = "${durationIntervals[4]}"

            println(durationIntervals)

        } else
            finish()


        if (filterData != null) {
            for (i in foodFilters?.categories!!.indices) {
                if (foodFilters?.categories!![i].id == filterData?.categoryId)
                    binding.spCategories.setSelection(i)
            }

            val seekValue = filterData?.calories ?: 1f
            if (seekValue < binding.seekBarCalories.valueFrom)
                binding.seekBarCalories.value = durationIntervals[0].toFloat()
            else
                binding.seekBarCalories.value = filterData?.calories ?: 1f
            setWorkoutDurationColors(
                filterData?.caloriesIntervalNo ?: 0
            )
        }
    }

    private fun initListeners() {
        binding.ivBack.setOnClickListener {
            AppUtils.preventDoubleClick(binding.ivBack)
            onBackPressed()
        }

        binding.seekBarCalories.addOnChangeListener { slider, value, fromUser ->
            // Responds to when slider's value is changed
            isFilterClear = false
            outer@ for (i in durationIntervals.indices) {
                if (i < durationIntervals.size - 1) {
                    if (value >= durationIntervals[i] && value <= durationIntervals[i + 1]) {
                        if (value <= (durationIntervals[i] + durationIntervals[i + 1]) / 2) {
                            setWorkoutDurationColors(i)
                            filterData?.caloriesIntervalNo = i
                            break@outer
                        } else {
                            setWorkoutDurationColors(i + 1)
                            filterData?.caloriesIntervalNo = i + 1
                            break@outer
                        }
                    }
//                    if (value.toInt() in durationIntervals[i]..durationIntervals[i + 1]) {
//
//                        if (i == durationIntervals.size - 2)
//                            filterData?.caloriesIntervalNo = durationIntervals.size - 1
//                        else
//                            filterData?.caloriesIntervalNo = i
//
//                        break@outer
//                    }
                } else {
                    setWorkoutDurationColors(i)
                }
            }

            filterData?.calories = value
        }


        binding.btnFindNow.setOnClickListener {
            AppUtils.preventDoubleClick(binding.btnFindNow)
            if (isFilterClear) {
                filterData = null
            }
            val intent = Intent()
            intent.putExtra("filterData", filterData)
            setResult(RESULT_OK, intent)
            finish()
        }
        binding.tvClearAll.setOnClickListener {
            setWorkoutDurationColors(0)
            binding.spCategories.setSelection(0)
            binding.seekBarCalories.value = binding.seekBarCalories.valueFrom
            filterData?.weight = ""
            weightAdapter?.notifyDataSetChanged()
            filterData?.foodType = ""
            foodTypeAdapter?.notifyDataSetChanged()
            isFilterClear = true
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initAdapters() {
        categoriesAdapter = SearchFilterSpinnerAdapter(this, foodFilters?.categories!!)
        binding.spCategories.adapter = categoriesAdapter

        binding.spCategories.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                filterData?.categoryId = foodFilters?.categories!![position].id
                isFilterClear = false
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        foodTypeAdapter = TagAdapters(foodFilters?.type!!, true,
            object : TagAdapters.TagAdapterInterface {
                override fun onItemClicked(position: Int) {
                    filterData?.foodType = foodFilters?.type?.get(position) ?: ""
                    foodTypeAdapter?.notifyDataSetChanged()
                    isFilterClear = false
                }
            })
        binding.rvFoodType.adapter = foodTypeAdapter

//        weightAdapter = TagAdapters(foodFilters?.weight_status!!, false,
//            object : TagAdapters.TagAdapterInterface {
//                override fun onItemClicked(position: Int) {
//                    filterData?.weight = foodFilters?.weight_status?.get(position) ?: ""
//                    weightAdapter?.notifyDataSetChanged()
//                    isFilterClear = false
//                }
//            })
//        binding.rvWeight.adapter = weightAdapter
    }


    private fun setWorkoutDurationColors(i: Int) {
        binding.tv10.setTextColor(resources.getColor(if (i == 0) R.color.blue_a700 else R.color.blue_grey_300))
        binding.tv15.setTextColor(resources.getColor(if (i == 1) R.color.blue_a700 else R.color.blue_grey_300))
        binding.tv20.setTextColor(resources.getColor(if (i == 2) R.color.blue_a700 else R.color.blue_grey_300))
        binding.tv25.setTextColor(resources.getColor(if (i == 3) R.color.blue_a700 else R.color.blue_grey_300))
        binding.tv30.setTextColor(resources.getColor(if (i == 4) R.color.blue_a700 else R.color.blue_grey_300))
    }

}