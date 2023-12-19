package com.cp.brittany.dixon.activities.home.search_filters

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.search.workout_tab.WorkoutFilterData
import com.cp.brittany.dixon.activities.home.search.workout_tab.WorkoutFilterResult
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivitySearchFiltersBinding
import com.cp.brittany.dixon.utils.AppUtils

class WorkoutFiltersActivity : BaseActivity() {
    private lateinit var binding: ActivitySearchFiltersBinding
    private var categoriesAdapter: SearchFilterSpinnerAdapter? = null

    private var durationIntervals: MutableList<Int> = ArrayList()

    private var filterData: WorkoutFilterResult? = null
    private var workoutFilters: WorkoutFilterData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchFiltersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()

        initViews()
        initListeners()

    }

    private fun initViews() {
        filterData = intent?.getParcelableExtra("filterData")
        workoutFilters = intent?.getParcelableExtra("filters")

        if (workoutFilters != null) {
            initAdapters()


            var interval = workoutFilters?.max_duration?.max_duration!! / 5
            if(interval < 1){
                interval = 1
            }
            var count = workoutFilters?.max_duration?.max_duration!!
            if(count < 5){
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

        } else
            finish()

        if (filterData != null) {
            for (i in workoutFilters?.categories!!.indices) {
                if (workoutFilters?.categories!![i].id == filterData?.categoryId)
                    binding.spCategories.setSelection(i)
            }

            if (filterData?.gender == "Male")
                binding.buttonGroup.setPosition(0, true)
            else
                binding.buttonGroup.setPosition(1, true)

            binding.seekBarDifficulty.value = filterData?.difficulty_value ?: 1f
            setDifficultyColors(filterData?.difficulty ?: 0)

            val seekValue = filterData?.duration_value ?: 1f
            if(seekValue < binding.seekBarDuration.valueFrom)
                binding.seekBarDuration.value = durationIntervals[0].toFloat()
            else
                binding.seekBarDuration.value = filterData?.duration_value ?: 1f
            setWorkoutDurationColors(
                filterData?.durationIntervalNo ?: 0
            )
        }


        binding.buttonGroup.setOnPositionChangedListener {
            filterData?.gender = if (it == 0) "Male" else "Female"
        }
    }

    private fun initListeners() {
        binding.ivBack.setOnClickListener {
            AppUtils.preventDoubleClick(binding.ivBack)
            onBackPressed()
        }

        binding.seekBarDifficulty.addOnChangeListener { slider, value, fromUser ->
            // Responds to when slider's value is changed
            when (value) {
                in 0.0..33.33 -> {
                    filterData?.difficulty = 0
                    setDifficultyColors(0)
                }
                in 33.33..66.33 -> {
                    filterData?.difficulty = 1
                    setDifficultyColors(1)
                }
                else -> {
                    filterData?.difficulty = 2
                    setDifficultyColors(2)
                }
            }

            filterData?.difficulty_value = value

        }

        binding.seekBarDuration.addOnChangeListener { slider, value, fromUser ->
            // Responds to when slider's value is changed

            outer@ for (i in durationIntervals.indices) {
                if (i < durationIntervals.size - 1) {

                    if(value >= durationIntervals[i] && value <= durationIntervals[i+1]){
                        if(value <= (durationIntervals[i]+durationIntervals[i+1])/2){
                            setWorkoutDurationColors(i)
                            filterData?.durationIntervalNo = i
                            break@outer
                        }
                        else{
                            setWorkoutDurationColors(i+1)
                            filterData?.durationIntervalNo = i+1
                            break@outer
                        }
                    }

//                    if (value.toInt() in durationIntervals[i]..durationIntervals[i + 1]) {
//                        setWorkoutDurationColors(i)
//
//                        if (i == durationIntervals.size - 2)
//                            filterData?.durationIntervalNo = durationIntervals.size - 1
//                        else
//                            filterData?.durationIntervalNo = i
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
            setDifficultyColors(0)
            setWorkoutDurationColors(0)
            binding.spCategories.setSelection(0)
            binding.buttonGroup.setPosition(0, true)
            binding.seekBarDuration.value = binding.seekBarDuration.valueFrom
            binding.seekBarDifficulty.value = 1F
            filterData = null

        }
    }

    private fun initAdapters() {
        categoriesAdapter = SearchFilterSpinnerAdapter(this, workoutFilters?.categories!!)
        binding.spCategories.adapter = categoriesAdapter

        binding.spCategories.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                filterData?.categoryId = workoutFilters?.categories!![position].id
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setDifficultyColors(i: Int) {
        binding.tvBeginner.setTextColor(resources.getColor(if (i == 0) R.color.blue_a700 else R.color.blue_grey_300))
        binding.tvModerate.setTextColor(resources.getColor(if (i == 1) R.color.blue_a700 else R.color.blue_grey_300))
        binding.tvExpert.setTextColor(resources.getColor(if (i == 2) R.color.blue_a700 else R.color.blue_grey_300))
    }

    private fun setWorkoutDurationColors(i: Int) {
        binding.tv10.setTextColor(resources.getColor(if (i == 0) R.color.blue_a700 else R.color.blue_grey_300))
        binding.tv15.setTextColor(resources.getColor(if (i == 1) R.color.blue_a700 else R.color.blue_grey_300))
        binding.tv20.setTextColor(resources.getColor(if (i == 2) R.color.blue_a700 else R.color.blue_grey_300))
        binding.tv25.setTextColor(resources.getColor(if (i == 3) R.color.blue_a700 else R.color.blue_grey_300))
        binding.tv30.setTextColor(resources.getColor(if (i == 4) R.color.blue_a700 else R.color.blue_grey_300))
    }


}