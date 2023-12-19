package com.cp.brittany.dixon.activities.home.search.food_tab

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.cp.brittany.dixon.activities.home.food_tab.RecipeDetailActivity
import com.cp.brittany.dixon.activities.home.models.SearchFoodModel
import com.cp.brittany.dixon.activities.home.models.SearchFoodResponse
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.databinding.FragmentSearchFoodBinding
import com.cp.brittany.dixon.loader.Loader
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.*
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call

class SearchFoodFragment : Fragment() {
    private lateinit var binding: FragmentSearchFoodBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>
    private lateinit var mContext: Context
    private var time = ""

    private var foodFilterList: MutableList<SearchFoodModel?> = ArrayList()

    private val myAdapter: SearchFoodAdapter by lazy {
        SearchFoodAdapter(requireContext(), object : SearchFoodAdapter.FoodClickListener {
            override fun onItemClick(position: Int) {
                val i = Intent(mContext, RecipeDetailActivity::class.java)
                //intent.putExtra("foodId", foodFilterList[position]?.id.toString())
                i.putExtra("foodId", foodFilterList[position]?.id.toString())
                i.putExtra("image", foodFilterList[position]?.image_path)
                i.putExtra("time", foodFilterList[position]?.time)
                i.putExtra("type", foodFilterList[position]?.type)
                i.putExtra("calories", foodFilterList[position]?.calories)
                i.putExtra("unit", foodFilterList[position]?.unit)
                i.putExtra("title", foodFilterList[position]?.name)
                i.putExtra("isBookmarked", foodFilterList[position]?.is_bookmarked)
                if (!foodFilterList[position]?.video.isNullOrEmpty())
                    i.putExtra("videoPath", foodFilterList[position]?.video_path)
                startActivity(i)
            }

        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSearchFoodBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = requireContext()
        retrofitClient = RetrofitClient.getClient(mContext).create(Api::class.java)

        initViews()
        observeApiResponse()
        setAdapter()

        getWorkOutFilterData()
    }

    private fun initViews() {
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        binding.lifecycleOwner = this
    }


    private fun setAdapter() {
        binding.rvFood.let {
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = myAdapter
        }
    }

    fun hitApi(search: String, filters: FoodFilterResult?) {
        if (filters == null)
            getWorkOutFilterData(search = search)
        else
            getWorkOutFilterData(
                filters.categoryId,
                filters.ingredientIds,
                filters.foodType,
                filters.weight,
                filters.calories.toInt(),
                search
            )
    }


    private fun getWorkOutFilterData(
        categoryId: Int = 0,
        ingredientIds: List<FoodFilterResultIngredient> = ArrayList(),
        foodType: String = "",
        weight: String = "",
        calories: Int = 0,
        search: String = ""
    ) {
        apiCall = retrofitClient.getFoodFilterData(categoryId, ingredientIds.toTypedArray(), foodType, weight, calories, search)
        viewModel.getFoodSearchFilter(apiCall)
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
                        ApiTags.SEARCH_FOOD_FILTER -> {
                            val model = Gson().fromJson(it.data.toString(), SearchFoodResponse::class.java)
                            myAdapter.myFoodSearchFilterList = model.data.toMutableList()
                            foodFilterList = model.data.toMutableList()
                            if (foodFilterList.isNullOrEmpty()) {
                                binding.rvFood.viewGone()
                                binding.llNoData.viewVisible()
                            } else {
                                binding.rvFood.viewVisible()
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