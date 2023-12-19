package com.cp.brittany.dixon.activities.home.faq_feedback.feedback_tab

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.models.FeedbackModel
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.databinding.FragmentFeedbackBinding
import com.cp.brittany.dixon.loader.Loader
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.ApiStatus
import com.cp.brittany.dixon.utils.AppUtils
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call

class FeedbackFragment : Fragment() {
    private lateinit var binding: FragmentFeedbackBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>
    private lateinit var mContext: Context
    private var rating: Int = -1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFeedbackBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = requireContext()
        retrofitClient = RetrofitClient.getClient(mContext).create(Api::class.java)
        initViews()
        initVM()
        observeApiResponse()
        initListeners()

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
        binding.ivVerySad.setOnClickListener {
            colorControl(0)
            rating = 1
            binding.layoutRating.setBackgroundResource(R.drawable.auth_edit_text_drawable_white)
            binding.tilRating.error = null
        }
        binding.ivSad.setOnClickListener {
            colorControl(1)
            rating = 2
            binding.layoutRating.setBackgroundResource(R.drawable.auth_edit_text_drawable_white)
            binding.tilRating.error = null
        }
        binding.ivNormal.setOnClickListener {
            colorControl(2)
            rating = 3
            binding.layoutRating.setBackgroundResource(R.drawable.auth_edit_text_drawable_white)
            binding.tilRating.error = null
        }
        binding.ivHappy.setOnClickListener {
            colorControl(3)
            rating = 4
            binding.layoutRating.setBackgroundResource(R.drawable.auth_edit_text_drawable_white)
            binding.tilRating.error = null
        }
        binding.ivVeryHappy.setOnClickListener {
            colorControl(4)
            rating = 5
            binding.layoutRating.setBackgroundResource(R.drawable.auth_edit_text_drawable_white)
            binding.tilRating.error = null
        }
        binding.btnSubmit.setOnClickListener {
            if(validate()){
                sendFeedback()
            }
        }

    }
    private fun validate(): Boolean {
        if(rating == -1){
            binding.layoutRating.setBackgroundResource(R.drawable.auth_edit_text_error)
            binding.tilRating.error = "Please select rating"

            return false
        }
        if (binding.etFeedback.text.toString().trim().isBlank()) {
            binding.etFeedback.setBackgroundResource(R.drawable.auth_edit_text_error)
            binding.tilFeedback.error = "Please enter something!"
            binding.tilFeedback.requestFocus()
            return false
        }

        return true
    }
    private fun colorControl(i: Int) {

        binding.ivVerySad.setImageResource(if (i == 0) R.drawable.ic_very_sad_filled else R.drawable.ic_very_sad)
        binding.ivSad.setImageResource(if (i == 1) R.drawable.ic_sad_filled else R.drawable.ic_sad)
        binding.ivNormal.setImageResource(if (i == 2) R.drawable.ic_normal_filled else R.drawable.ic_normal)
        binding.ivHappy.setImageResource(if (i == 3) R.drawable.ic_happy_filled else R.drawable.ic_happy)
        binding.ivVeryHappy.setImageResource(if (i == 4) R.drawable.ic_very_happy_filled else R.drawable.ic_very_happy)

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
                    AppUtils.showToast(requireActivity(), it.message!!, false)
                }
                ApiStatus.SUCCESS -> {
                    Loader.hideLoader()
                    when (it.tag) {
                        ApiTags.SEND_FEEDBACK -> {
                            val model = Gson().fromJson(it.data.toString(), FeedbackModel::class.java)
                            AppUtils.showToast(requireActivity(), model.message!!, true)

                        }
                    }
                }
            }
        })
    }
    private fun sendFeedback() {
        apiCall = retrofitClient.sendFeedback(
            binding.etFeedback.text.toString(),
            rating
        )
        viewModel.sendFeedback(apiCall)
    }
}