package com.cp.brittany.dixon.activities.home.shipping_address

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivitySearchableSpinnerBinding
import com.cp.brittany.dixon.utils.viewGone
import com.cp.brittany.dixon.utils.viewVisible

class SearchableSpinnerActivity : BaseActivity() {
    private lateinit var binding: ActivitySearchableSpinnerBinding
    private lateinit var adapter: SearchableSpinnerAdapter

    private var title = ""
    private var selectedId = -1
    private var searchableList: ArrayList<SearchableSpinnerModel> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchableSpinnerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        blackStatusBarIcons()

        initViews()
        initListeners()
    }

    private fun initViews() {
        title = intent?.getStringExtra("title") ?: ""
        searchableList = intent?.getParcelableArrayListExtra("listData") ?: ArrayList()

        binding.topBar.tvScreenTitle.text = title
        binding.topBar.ivBack.viewVisible()

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isBlank())
                    binding.ivCross.viewGone()
                else
                    binding.ivCross.viewVisible()
                adapter.filter.filter(s.toString())
            }
        })

        initAdapter()
    }

    private fun initListeners() {
        binding.topBar.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.ivCross.setOnClickListener {
            binding.etSearch.setText("")
        }
    }

    private fun initAdapter() {
        adapter = SearchableSpinnerAdapter(
            this, searchableList,
            object : SearchableSpinnerAdapter.SearchableSpinnerInterface {
                override fun onItemClicked(selectedId: Int) {
                    val returnIntent = Intent()
                    searchableList.forEachIndexed { _, searchableSpinnerModel ->
                        if (searchableSpinnerModel.id == selectedId) {
                            returnIntent.putExtra("selectedId", searchableSpinnerModel.id)
                            returnIntent.putExtra("selectedValue", searchableSpinnerModel.value)
                        }
                    }
                    setResult(RESULT_OK, returnIntent)
                    finish()
                }
            })
        binding.rvSearchedResults.adapter = adapter
    }

    override fun onBackPressed() {
        super.onBackPressed()

        val returnIntent = Intent()
        setResult(RESULT_CANCELED, returnIntent)
        finish()
    }
}