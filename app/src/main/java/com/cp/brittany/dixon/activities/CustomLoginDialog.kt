package com.cp.brittany.dixon.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.cp.brittany.dixon.activities.auth.login.SignInActivity
import com.cp.brittany.dixon.databinding.LoginDialogBinding

class CustomLoginDialog(private val isFromCart: Boolean, private val activity: Activity?) : DialogFragment() {
    private lateinit var binding: LoginDialogBinding
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = LoginDialogBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        setCancelable(true)
//        initListeners()
//    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)

        return dialog
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = LoginDialogBinding.inflate(inflater, container, false)
        initListeners()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
    private fun initListeners() {
        binding.btnOk.setOnClickListener {
            val i = Intent(activity, SignInActivity::class.java)
            i.putExtra("isFromCart", isFromCart)
            activity?.startActivity(i)
            dismiss()
        }
    }


}
