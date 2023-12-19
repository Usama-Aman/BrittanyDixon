package com.cp.brittany.dixon.activities.home.profile_tab

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.HomeActivity
import com.cp.brittany.dixon.base.longDateFormat
import com.cp.brittany.dixon.databinding.FragmentActivityBinding
import com.cp.brittany.dixon.utils.AppUtils
import java.text.SimpleDateFormat
import java.util.*

class ActivityFragment : Fragment() {

    private lateinit var binding: FragmentActivityBinding

    private val mCalendar: Calendar = Calendar.getInstance()
    private var selectedMonth = -1
    private var selectedYear = -1
    private var currentMonth = -1
    private var selectedDate: String = ""
    private var currentDay = -1
    private lateinit var date: DatePickerDialog.OnDateSetListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentActivityBinding.inflate(layoutInflater,container,false)
        initViews()
        initListeners()
        return binding.root
    }

    @SuppressLint("SimpleDateFormat")
    private fun initViews(){
//        binding.ivNext.isClickable = false
//        binding.ivPrevious.isClickable = false
//        binding.tvDate.isClickable = false
//        binding.tvDay.isClickable = false
        binding.pbMove.setProgressWithAnimation(70f, 1000)
        binding.pbStand.setProgressWithAnimation(60f, 1000)
        binding.pbExercise.setProgressWithAnimation(40f, 1000)
        val c = Calendar.getInstance()
        selectDate()
        currentDay = c.get(Calendar.DAY_OF_MONTH)
        selectedYear = c.get(Calendar.YEAR)
        currentMonth = c[Calendar.MONTH]
        selectedMonth = currentMonth

        val df = SimpleDateFormat("MM-dd-yyyy")
        binding.tvDate.text = (activity as HomeActivity).longDateFormat(c.time)
        binding.tvDay.text = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH)
        selectedDate = df.format(c.time)
    }

    private fun initListeners(){
        binding.tvDate.setOnClickListener {
            AppUtils.preventDoubleClick(binding.tvDate)
            if(!binding.tvDate.isClickable){
                return@setOnClickListener
            }
            val datepicker = DatePickerDialog(
                requireContext(), R.style.MyDatePickerStyle, date, mCalendar
                    .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)
            )
            datepicker.datePicker.maxDate = System.currentTimeMillis()
            datepicker.show()

        }

        binding.tvDay.setOnClickListener {
            AppUtils.preventDoubleClick(binding.tvDay)
            if(!binding.tvDay.isClickable){
                return@setOnClickListener
            }
            val datepicker = DatePickerDialog(
                requireContext(), R.style.MyDatePickerStyle, date, mCalendar
                    .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)
            )
            datepicker.datePicker.maxDate = System.currentTimeMillis()
            datepicker.show()

        }

        binding.ivPrevious.setOnClickListener {
            AppUtils.preventDoubleClick(binding.ivPrevious)
            if(!binding.ivPrevious.isClickable){
                return@setOnClickListener
            }
//            binding.ivNext.isClickable = false
//            binding.ivPrevious.isClickable = false
//            binding.tvDate.isClickable = false
//            binding.tvDay.isClickable = false
            mCalendar.set(Calendar.DAY_OF_MONTH, mCalendar[Calendar.DAY_OF_MONTH]-1)
            currentMonth = mCalendar[Calendar.MONTH]
            selectedMonth = currentMonth
            selectedYear = mCalendar[Calendar.YEAR]
            currentDay = mCalendar.get(Calendar.DAY_OF_MONTH)
            binding.tvDate.text = (activity as HomeActivity).longDateFormat(mCalendar.time)
            binding.tvDay.text = mCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH)
            selectedDate = AppUtils.convertToCustomFormat(mCalendar.time.toString())
        }

        binding.ivNext.setOnClickListener {
            AppUtils.preventDoubleClick(binding.ivNext)
            if(!binding.ivNext.isClickable || AppUtils.convertToCustomFormat(mCalendar.time.toString()) == AppUtils.convertToCustomFormat(Calendar.getInstance().time.toString())){
                return@setOnClickListener
            }
//            binding.ivNext.isClickable = false
//            binding.ivPrevious.isClickable = false
//            binding.tvDate.isClickable = false
//            binding.tvDay.isClickable = false
            mCalendar.set(Calendar.DAY_OF_MONTH, mCalendar[Calendar.DAY_OF_MONTH]+1)
            currentMonth = mCalendar[Calendar.MONTH]
            selectedMonth = currentMonth
            selectedYear = mCalendar[Calendar.YEAR]
            currentDay = mCalendar.get(Calendar.DAY_OF_MONTH)
            binding.tvDate.text = (activity as HomeActivity).longDateFormat(mCalendar.time)
            binding.tvDay.text = mCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH)
            selectedDate = AppUtils.convertToCustomFormat(mCalendar.time.toString())
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun selectDate() {
        date =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                mCalendar.set(Calendar.YEAR, year)
                mCalendar.set(Calendar.MONTH, monthOfYear)
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                currentMonth = mCalendar[Calendar.MONTH]
                selectedMonth = currentMonth
                selectedYear = mCalendar[Calendar.YEAR]
                currentDay = mCalendar.get(Calendar.DAY_OF_MONTH)
                binding.tvDate.text = (activity as HomeActivity).longDateFormat(mCalendar.time)
                binding.tvDay.text = mCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH)
                selectedDate = AppUtils.convertToCustomFormat(mCalendar.time.toString())
                Log.e("Current date", selectedDate)
//                binding.ivNext.isClickable = false
//                binding.ivPrevious.isClickable = false
//                binding.tvDate.isClickable = false
//                binding.tvDay.isClickable = false
            }


    }
}