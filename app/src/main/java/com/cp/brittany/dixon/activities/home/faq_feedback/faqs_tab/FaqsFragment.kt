package com.cp.brittany.dixon.activities.home.faq_feedback.faqs_tab

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.cp.brittany.dixon.activities.home.models.FaqsData
import com.cp.brittany.dixon.activities.home.models.FaqsModel
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.databinding.FragmentFaqsBinding
import com.cp.brittany.dixon.loader.Loader
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.ApiStatus
import com.cp.brittany.dixon.utils.AppUtils
import com.cp.brittany.dixon.utils.viewGone
import com.cp.brittany.dixon.utils.viewVisible
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import java.util.*
import kotlin.collections.ArrayList

class FaqsFragment : Fragment() {
    private lateinit var binding: FragmentFaqsBinding
    private lateinit var faqsAdapter: FaqsAdapter
    private var list: MutableList<FaqsData> = ArrayList()
    private var filterList: MutableList<FaqsData> = ArrayList()
    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFaqsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = requireContext()
        retrofitClient = RetrofitClient.getClient(mContext).create(Api::class.java)
        initViews()
        initVM()
        observeApiResponse()
        initAdapters()
        initListeners()
        getFaqs()
    }
    private fun initVM() {
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        binding.lifecycleOwner = this
    }
    private fun initViews() {

    }

    private fun initListeners() {
        binding.etSearch.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                applyFilters()
            }
            true
        })
    }

    private fun initAdapters() {
        faqsAdapter = FaqsAdapter()
        binding.rvFaqs.adapter = faqsAdapter
    }
    private fun observeApiResponse() {
        viewModel.getApiResponse().observe(requireActivity(), {
            when (it.status) {
                ApiStatus.LOADING -> {
                    Loader.showLoader((requireActivity() as AppCompatActivity)) {
                        if (this::apiCall.isInitialized)
                            apiCall.cancel()
                    }
                }
                ApiStatus.ERROR -> {
                    Loader.hideLoader()
                    if(it.message == "No data found..."){
                        binding.rvFaqs.viewGone()
                        binding.noData.viewVisible()
                    }
                    else
                        AppUtils.showToast(requireActivity(), it.message!!, false)
                }
                ApiStatus.SUCCESS -> {
                    Loader.hideLoader()
                    when (it.tag) {
                        ApiTags.GET_FAQS -> {
                            binding.rvFaqs.viewVisible()
                            binding.noData.viewGone()
                            val model = Gson().fromJson(it.data.toString(), FaqsModel::class.java)
                            setFaqs(model)
                        }
                    }
                }
            }
        })
    }

    private fun setFaqs(model: FaqsModel) {
        if (model != null && model.data != null) {
            list.clear()
            list.addAll(model.data)
            faqsAdapter.setFaqs(list)
            binding.rvFaqs.adapter?.notifyDataSetChanged()
            if(list.isEmpty()){
                binding.noData.visibility = View.VISIBLE
            }
            else{
                binding.noData.visibility = View.GONE
            }
        }

    }
    private fun getFaqs() {
        apiCall = retrofitClient.getFaqs(0)
        viewModel.getFaqs(apiCall)
    }
    private fun applyFilters(){
        filterList.clear()
        if(binding.etSearch.text.toString().trim().isEmpty()){
            faqsAdapter.setFaqs(list)
            binding.rvFaqs.adapter?.notifyDataSetChanged()
            if(list.isEmpty()){
                binding.noData.visibility = View.VISIBLE
            }
            else{
                binding.noData.visibility = View.GONE
            }
            return
        }
        for (item in list){
            if(item.question.contains(binding.etSearch.text.toString(), ignoreCase = true) || item.answer.contains(binding.etSearch.text.toString(), ignoreCase = true)){
                filterList.add(item)
            }
        }
        if(filterList.isEmpty()){
            binding.noData.visibility = View.VISIBLE
        }
        else{
            binding.noData.visibility = View.GONE
        }
        faqsAdapter.setFaqs(filterList)
        binding.rvFaqs.adapter?.notifyDataSetChanged()
    }
}