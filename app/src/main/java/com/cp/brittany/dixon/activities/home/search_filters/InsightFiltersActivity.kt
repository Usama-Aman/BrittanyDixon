package com.cp.brittany.dixon.activities.home.search_filters

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.search.insight_tab.InsightFilterData
import com.cp.brittany.dixon.activities.home.search.insight_tab.InsightFilterResult
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivityInsightFiltersBinding
import com.cp.brittany.dixon.utils.AppUtils

class InsightFiltersActivity : BaseActivity() {
    private lateinit var binding: ActivityInsightFiltersBinding
    private var categoriesAdapter: SearchFilterSpinnerAdapter? = null

    private var filterData: InsightFilterResult? = null
    private var insightFilters: InsightFilterData? = null
    private var durationIntervals: MutableList<Int> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsightFiltersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()

        initViews()
        initListeners()
    }

    private fun initViews() {
        filterData = intent?.getParcelableExtra("filterData")
        insightFilters = intent?.getParcelableExtra("filters")

        if (insightFilters != null) {
            initAdapters()


            var interval = (insightFilters?.max_duration?.toDouble()!! / 5).toInt()
            if (interval < 1) {
                interval = 1
            }
            var count = insightFilters?.max_duration?.toDouble()?.toInt()!!
            if (count < 5) {
                count = 5
            }
            //durationIntervals.add(1)
            outer@ for (i in 0..count step interval)
                if (i + interval <= count)
                    durationIntervals.add(i + interval)
                else
                    break@outer
            binding.seekBarDuration.valueFrom = durationIntervals[0].toFloat()
            binding.seekBarDuration.valueTo = count.toFloat()
            binding.seekBarDuration.stepSize = interval.toFloat()
            binding.tv10.text = "${durationIntervals[0]}"
            binding.tv15.text = "${durationIntervals[1]}"
            binding.tv20.text = "${durationIntervals[2]}"
            binding.tv25.text = "${durationIntervals[3]}"
            binding.tv30.text = "${durationIntervals[4]}"

            println(durationIntervals)
        }

        if (filterData != null) {
            for (i in insightFilters?.categories!!.indices) {
                if (insightFilters?.categories!![i].id == filterData?.categoryId)
                    binding.spCategories.setSelection(i)
            }
            val seekValue = filterData?.duration_value ?: 1f
            if (seekValue < binding.seekBarDuration.valueFrom)
                binding.seekBarDuration.value = durationIntervals[0].toFloat()
            else
                binding.seekBarDuration.value = filterData?.duration_value ?: 1f
            setWorkoutDurationColors(
                filterData?.duration_interval ?: 0
            )
        }
    }

    private fun initListeners() {
        binding.ivBack.setOnClickListener {
            AppUtils.preventDoubleClick(binding.ivBack)
            onBackPressed()
        }

        binding.seekBarDuration.addOnChangeListener { slider, value, fromUser ->
            // Responds to when slider's value is changed

            outer@ for (i in durationIntervals.indices) {
                if (i < durationIntervals.size - 1) {
                    if (value >= durationIntervals[i] && value <= durationIntervals[i + 1]) {
                        if (value <= (durationIntervals[i] + durationIntervals[i + 1]) / 2) {
                            setWorkoutDurationColors(i)
                            filterData?.duration_interval = i
                            break@outer
                        } else {
                            setWorkoutDurationColors(i + 1)
                            filterData?.duration_interval = i + 1
                            break@outer
                        }
                    }
//                    if (value.toInt() in durationIntervals[i]..durationIntervals[i + 1]) {
//
//                        if (i == durationIntervals.size - 2)
//                            filterData?.duration_interval = durationIntervals.size - 1
//                        else
//                            filterData?.duration_interval = i
//
//                        break@outer
//                    }
                } else {
                    setWorkoutDurationColors(i)
                }
            }

            filterData?.duration_value = value
        }

        binding.btnFindNow.setOnClickListener {
            AppUtils.preventDoubleClick(binding.btnFindNow)
            val intent = Intent()
            intent.putExtra("filterData", filterData)
            setResult(RESULT_OK, intent)
            finish()
        }
        binding.tvClearAll.setOnClickListener {
            setWorkoutDurationColors(0)
            binding.spCategories.setSelection(0)
            binding.seekBarDuration.value = binding.seekBarDuration.valueFrom
            filterData = null

        }
    }


    private fun initAdapters() {
        categoriesAdapter = SearchFilterSpinnerAdapter(this, insightFilters?.categories!!)
        binding.spCategories.adapter = categoriesAdapter

        binding.spCategories.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                filterData?.categoryId = insightFilters?.categories!![position].id
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }


    private fun setWorkoutDurationColors(i: Int) {
        binding.tv10.setTextColor(resources.getColor(if (i == 0) R.color.blue_a700 else R.color.blue_grey_300))
        binding.tv15.setTextColor(resources.getColor(if (i == 1) R.color.blue_a700 else R.color.blue_grey_300))
        binding.tv20.setTextColor(resources.getColor(if (i == 2) R.color.blue_a700 else R.color.blue_grey_300))
        binding.tv25.setTextColor(resources.getColor(if (i == 3) R.color.blue_a700 else R.color.blue_grey_300))
        binding.tv30.setTextColor(resources.getColor(if (i == 4) R.color.blue_a700 else R.color.blue_grey_300))
    }

}