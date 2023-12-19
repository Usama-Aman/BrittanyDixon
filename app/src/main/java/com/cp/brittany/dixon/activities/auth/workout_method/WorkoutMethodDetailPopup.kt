package com.cp.brittany.dixon.activities.auth.workout_method

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cp.brittany.dixon.activities.auth.models.PreferencesData
import com.cp.brittany.dixon.activities.auth.models.ProfilesData
import com.cp.brittany.dixon.databinding.FragmentWorkoutMethodDetailPopupBinding


class WorkoutMethodDetailPopup(val profileData: List<ProfilesData>, val preferenceData: PreferencesData) : DialogFragment() {

    private lateinit var binding: FragmentWorkoutMethodDetailPopupBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWorkoutMethodDetailPopupBinding.inflate(layoutInflater, container, false)
        initViews()
        initAdapter()
        return binding.root
    }

    private fun initViews() {
        binding.name.text = preferenceData.name
        binding.tvDescription.text = preferenceData.description

        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    private fun initAdapter() {
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = WorkoutPopupAdapter(profileData)
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
            lp.height = (displayMetrics.heightPixels * 0.70).toInt()
            bottomSheetDialog.window?.attributes = lp
        }
    }

}