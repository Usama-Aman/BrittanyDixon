package com.cp.brittany.dixon.activities.home.change_email

import android.os.Bundle
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivityChangeEmailBinding

class ChangeEmailActivity : BaseActivity() {

    private lateinit var binding: ActivityChangeEmailBinding
    private lateinit var email: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()

        initViews()

    }

    private fun initViews() {
        if(intent != null){
            email = intent.getStringExtra("email")!!
            binding.etEmail.setText(email)
        }
    }



}