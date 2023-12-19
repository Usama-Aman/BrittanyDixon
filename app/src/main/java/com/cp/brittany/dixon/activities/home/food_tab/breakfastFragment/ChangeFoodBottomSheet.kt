package com.cp.brittany.dixon.activities.home.food_tab.breakfastFragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.home_models.FoodsList
import com.cp.brittany.dixon.databinding.LayoutBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class ChangeFoodBottomSheet(
    private val foodType: String,
    private val topFoodList: MutableList<FoodsList>,
    private val changeFoodInterface: ChangeFoodInterface
) : BottomSheetDialogFragment() {

    private lateinit var binding: LayoutBottomSheetBinding
    private lateinit var changeFoodBottomAdapter: ChangeFoodBottomSheetAdapter

    interface ChangeFoodInterface {
        fun onNewFoodSelected(position: Int)
        fun onRemoveFoodSelected(position: Int)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.BottomSheetDialogTheme)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.BottomDialogAnimation
    }

    override fun onStart() {
        super.onStart()
        val bottomSheetDialog = dialog
        if (bottomSheetDialog != null) {

            val lp = WindowManager.LayoutParams()
            lp.copyFrom(dialog?.window?.attributes)
            val displayMetrics = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)

            lp.width = (displayMetrics.widthPixels).toInt()
//          lp.height = (displayMetrics.heightPixels * 0.80).toInt()
            bottomSheetDialog.window?.attributes = lp
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.layout_bottom_sheet, container, false)
        binding = LayoutBottomSheetBinding.inflate(layoutInflater, container, false)

        initViews()

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun initViews() {
        binding.title.text = "Add $foodType"

        binding.searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                changeFoodBottomAdapter.filter.filter(s.toString())
            }

        })

        changeFoodBottomAdapter =
            ChangeFoodBottomSheetAdapter(topFoodList, object : ChangeFoodBottomSheetAdapter.ChangeFoodBottomInterface {
                override fun onItemClicked(position: Int) {
                    dismiss()
                    changeFoodInterface.onNewFoodSelected(position)
                }

                override fun onItemRemovedClicked(position: Int) {
                    dismiss()
                    changeFoodInterface.onRemoveFoodSelected(position)
                }
            })
        binding.recyclerView.adapter = changeFoodBottomAdapter
    }

}