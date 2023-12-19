package com.cp.brittany.dixon.activities

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.cp.brittany.dixon.databinding.CustomConfirmationDialogBinding

class ConfirmationDialog(
    private val title: String,
    private val data: String,
    private val isSingleButton: Boolean,
    activity: Activity?,
    private val alertButtonClickListener: OnAlertOneButtonClickListener
) : DialogFragment() {

    private lateinit var binding: CustomConfirmationDialogBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)
        binding = CustomConfirmationDialogBinding.inflate(layoutInflater)
        initListeners()
        setData()
        return  binding.root
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
    private fun initListeners() {
        binding.btnOk.setOnClickListener {
            alertButtonClickListener.okClickListener()
            dismiss()
        }
        binding.btnCancel.setOnClickListener {
            alertButtonClickListener.cancelClickListener()
            dismiss()
        }
    }

    private fun setData() {
        binding.tvDescription.text = data
        binding.tvTitle.text = title
        if (isSingleButton) {
            binding.btnCancel.visibility = View.GONE
        }
    }


}

interface OnAlertOneButtonClickListener {
    fun okClickListener()
    fun cancelClickListener()
}
