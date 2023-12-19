package com.cp.brittany.dixon.activities.home.search.workout_tab

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.cp.brittany.dixon.activities.home.models.WorkoutSearchFilterData
import com.cp.brittany.dixon.activities.home.models.WorkoutSearchResponse
import com.cp.brittany.dixon.activities.home.workout_detail.WorkoutDetailActivity
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.databinding.FragmentSearchWorkoutBinding
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


class SearchWorkoutFragment : Fragment() {
    private lateinit var binding: FragmentSearchWorkoutBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>
    private lateinit var mContext: Context

    private var workoutList: MutableList<WorkoutSearchFilterData?> = ArrayList()

    private val myAdapter: SearchWorkoutAdapter by lazy {
        SearchWorkoutAdapter(requireContext(), object : SearchWorkoutAdapter.WorkoutSearchInterface {
            override fun onItemClicked(position: Int) {
                val model = workoutList[position]
                val intent = Intent(mContext, WorkoutDetailActivity::class.java)
                intent.putExtra("workoutId", model?.id.toString())
                intent.putExtra("workoutImage", model?.image_path)
                intent.putExtra("title", model?.name)
                intent.putExtra("details", model?.description)
                intent.putExtra("weight", model?.weight_status)
                intent.putExtra("level", model?.level)
                intent.putExtra("percentageCompleted", model?.percentage_completed)
                intent.putExtra("isBookmarked", model?.is_bookmarked)
                intent.putExtra("time", model?.duration)
                intent.putExtra("unit", model?.unit)
                intent.putExtra("is_free", model?.is_free)
                intent.putExtra("calories", model?.cal_gain_reduce)
                startActivity(intent)
            }

        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchWorkoutBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = requireContext()
        retrofitClient = RetrofitClient.getClient(mContext).create(Api::class.java)

        initVM()
        observeApiResponse()
        setAdapter()

        getWorkOutFilterData()
    }

    private fun initVM() {
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        binding.lifecycleOwner = this
    }


    private fun setAdapter() {
        binding.rvWorkout.let {
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = myAdapter
        }
    }

    fun hitApi(search: String, filters: WorkoutFilterResult?) {
        if (filters == null)
            getWorkOutFilterData(search = search)
        else
            getWorkOutFilterData(filters.categoryId, filters.difficulty, filters.gender, filters.duration_value.toInt(), search)
    }

    private fun getWorkOutFilterData(categoryId: Int = 0, level: Int = 0, gender: String = "", duration: Int = 0, search: String = "") {
        apiCall = retrofitClient.getWorkOutData(categoryId, level, gender, duration, search)
        viewModel.getWorkoutSearchFilter(apiCall)
    }

    private fun observeApiResponse() {
        viewModel.getApiResponse().observe(requireActivity(), {
            when (it.status) {
                ApiStatus.LOADING -> {
                    if (it.tag != ApiTags.GET_WORKOUT_FILTERS)
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
                        ApiTags.SEARCH_WORKOUT_FILTER -> {
                            val model = Gson().fromJson(it.data.toString(), WorkoutSearchResponse::class.java)
                            myAdapter.myWorkoutSearchFilterList = model.data.toMutableList()
                            workoutList = model.data.toMutableList()
                            if (workoutList.isNullOrEmpty()) {
                                binding.rvWorkout.viewGone()
                                binding.llNoData.viewVisible()
                            } else {
                                binding.rvWorkout.viewVisible()
                                binding.llNoData.viewGone()

                            }
                            setAdapter()
                        }
                    }
                }
            }
        })
    }
}