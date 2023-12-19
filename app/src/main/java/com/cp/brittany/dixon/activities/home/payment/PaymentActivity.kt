package com.cp.brittany.dixon.activities.home.payment

import android.os.Bundle
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.models.Card
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivityPaymentBinding
import com.cp.brittany.dixon.utils.maskeditor.MaskTextWatcher
import java.text.DecimalFormat
import java.text.NumberFormat

class PaymentActivity : BaseActivity() {
    private lateinit var binding: ActivityPaymentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()

        initViews()
        initListeners()
    }

    private fun initViews() {

        if(intent != null && intent.hasExtra("card")){
            val formatter: NumberFormat = DecimalFormat("00")
            val card: Card = intent.getParcelableExtra("card")!!
            val year = (card.exp_year % 1000).toString()
            val month = formatter.format(card.exp_month)
            binding.etDate.setText("$month/$year")
            binding.etCardNumber.setText("**** **** **** ${card.last4}")
            binding.etCVV.setText("***")
        }

        val textWatcherNumber = MaskTextWatcher(binding.etCardNumber, "#### #### #### ####")
        val textWatcherDate = MaskTextWatcher(binding.etDate, "##/##")
        binding.etDate.addTextChangedListener(textWatcherDate)
        binding.etCardNumber.addTextChangedListener(textWatcherNumber)
    }

    private fun initListeners() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

    }
}