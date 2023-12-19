package com.cp.brittany.dixon.activities.home.checkout_payment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivityAddNewCardBinding
import com.google.android.material.textfield.TextInputLayout
//import com.stripe.android.Stripe
import java.util.*

class AddNewCardActivity : BaseActivity() {

    private lateinit var binding: ActivityAddNewCardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewCardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()

    }

    private fun setEditText(editText: EditText, layout: TextInputLayout) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                editText.setBackgroundResource(R.drawable.auth_edit_text_drawable)
                layout.error = null
                layout.isErrorEnabled = false
            }
        })
    }

}