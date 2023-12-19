package com.cp.brittany.dixon.activities.home.food_tab

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.util.Pair
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.food_tab.breakfastFragment.ChangeFoodBottomSheet
import com.cp.brittany.dixon.activities.home.food_tab.breakfastFragment.FindRecipesAdapter
import com.cp.brittany.dixon.activities.home.food_tab.breakfastFragment.RecipeClickListener
import com.cp.brittany.dixon.activities.home.home_models.AllFoodsModel
import com.cp.brittany.dixon.activities.home.home_models.FoodSimpleSearchModel
import com.cp.brittany.dixon.activities.home.home_models.FoodsList
import com.cp.brittany.dixon.activities.home.models.ChangeFoodStatusModel
import com.cp.brittany.dixon.activities.home.search.SimpleFoodSearchActivity
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.databinding.FragmentFoodBinding
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.ApiStatus
import com.cp.brittany.dixon.utils.AppUtils
import com.astritveliu.boom.Boom
import com.bumptech.glide.Glide
import com.cp.brittany.dixon.activities.home.HomeActivity
import com.cp.brittany.dixon.activities.home.models.InsightBookMarkModel
import com.cp.brittany.dixon.base.BaseActivityResult
import com.cp.brittany.dixon.databinding.ItemFoodSliderBinding
import com.cp.brittany.dixon.utils.viewGone
import com.cp.brittany.dixon.utils.viewVisible
import com.faltenreich.skeletonlayout.Skeleton
import com.google.android.material.transition.MaterialFadeThrough
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import androidx.core.widget.NestedScrollView


class FoodFragment : Fragment(), OnItemClickListener {
    private lateinit var binding: FragmentFoodBinding
    private lateinit var findRecipesAdapter: FindRecipesAdapter
    private val foodsList: MutableList<FoodsList> = ArrayList()
    private var mAdapter: ViewSliderAdapter? = null

    private lateinit var mContext: Context
    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>

    private var topFoodDetail: FoodsList? = null
    private var topFoodList: MutableList<FoodsList> = ArrayList()
    private var bottomFoodList: MutableList<FoodsList> = ArrayList()
    var foodId = ""
    private var foodType = ""
    private lateinit var skeletonLayout: Skeleton
    private var isPullRefresh = false
    private var selectedBookmark = -1
    private var isGuest = 0
    private var canLoadMore = false
    private var skip = 0
    private var take = 10
    val activityLauncher: BaseActivityResult<Intent, ActivityResult> =
        BaseActivityResult.registerActivityForResult(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFoodBinding.inflate(layoutInflater, container, false)
        skeletonLayout = binding.skeletonLayout
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        retrofitClient = RetrofitClient.getClient(requireContext()).create(Api::class.java)
        mContext = requireContext()

        initVM()
        observeApiResponse()
        initViews()
        initListener()
        initAdapters()

    }

    private fun initVM() {
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        binding.lifecycleOwner = this
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeApiResponse() {
        viewModel.getApiResponse().observe(requireActivity(), {
            when (it.status) {
                ApiStatus.LOADING -> {
//                    if (it.tag != ApiTags.GET_ALL_FOODS)
//                        Loader.showLoader((requireActivity() as AppCompatActivity)) {
//                            if (this@FoodFragment::apiCall.isInitialized)
//                                apiCall.cancel()
//                        }
                }
                ApiStatus.ERROR -> {
                    //Loader.hideLoader()
                    AppUtils.showToast(requireActivity(), it.message!!, false)
                }
                ApiStatus.SUCCESS -> {
                    //Loader.hideLoader()
                    when (it.tag) {
                        ApiTags.GET_ALL_FOODS -> {
                            Handler(Looper.getMainLooper()).postDelayed({
                                binding.mainLayout.viewVisible()
                                binding.skeletonLayout.viewGone()
                                //binding.skeletonLayoutScroll.viewGone()
                                skeletonLayout.showOriginal()
                                binding.pullToRefresh.isRefreshing = false
                            }, 1000)


                            val model =
                                Gson().fromJson(it.data.toString(), AllFoodsModel::class.java)
                            setAllFoods(model)
                        }
                        ApiTags.GET_FEATURED_FOOD_BY_TYPE -> {
                            val model =
                                Gson().fromJson(it.data.toString(), AllFoodsModel::class.java)
                            topFoodList.clear()
                            topFoodList.addAll(model.data.foods)
                            mAdapter?.notifyDataSetChanged()
                            if (topFoodList.isNotEmpty()) {
                                topFoodDetail = model?.data?.foods!![0]
                                //binding.viewPager.viewVisible()

                            } else {
                                //topFoodList.add(topFoodDetail!!)
                                topFoodDetail = null
                                //binding.viewPager.viewGone()
                            }

                            if (foodsList.isEmpty())
                                getAllFoods()
                            else {
                                getFoodsByType(foodType)
                                Handler(Looper.getMainLooper()).postDelayed({
                                    binding.mainLayout.viewVisible()
                                    binding.skeletonLayout.viewGone()
                                    //binding.skeletonLayoutScroll.viewGone()
                                    skeletonLayout.showOriginal()
                                    binding.pullToRefresh.isRefreshing = false
                                    isPullRefresh = false
                                }, 1000)
                            }
                        }
                        ApiTags.GET_FOOD_BY_TYPE -> {


                            val model =
                                Gson().fromJson(it.data.toString(), AllFoodsModel::class.java)
                            bottomFoodList.clear()
                            bottomFoodList.addAll(model.data.foods)
                            if (bottomFoodList.isNotEmpty()) {
                                topFoodList.forEach {
                                    bottomFoodList.forEach { item ->
                                        if (it.id == item.id) {
                                            item.isAddedToUserFoods = true
                                        }
                                    }
                                }
                            }
                            Handler(Looper.getMainLooper()).postDelayed({
                                binding.mainLayout.viewVisible()
                                binding.skeletonLayout.viewGone()
                                //binding.skeletonLayoutScroll.viewGone()
                                skeletonLayout.showOriginal()
                                binding.pullToRefresh.isRefreshing = false
                                isPullRefresh = false
                            }, 1000)

                        }
                        ApiTags.FOOD_SIMPLE_SEARCH -> {
                            Handler(Looper.getMainLooper()).postDelayed({
                                binding.mainLayout.viewVisible()
                                binding.skeletonLayout.viewGone()
                                //binding.skeletonLayoutScroll.viewGone()
                                skeletonLayout.showOriginal()
                                binding.pullToRefresh.isRefreshing = false
                                isPullRefresh = false
                            }, 1000)

                            val model =
                                Gson().fromJson(it.data.toString(), FoodSimpleSearchModel::class.java)
                            foodsList.clear()
                            foodsList.addAll(model.data)
                            findRecipesAdapter.notifyDataSetChanged()
                        }
                        ApiTags.CHANGE_FOOD_STATUS -> {

                            val model = Gson().fromJson(it.data.toString(), ChangeFoodStatusModel::class.java)
                            AppUtils.showToast(requireActivity(), model.message, true)

                            getFeaturedFood(foodType)
                        }
                        ApiTags.ADD_REMOVE_FOOD_TO_USER_FOODS -> {

                            //val model = Gson().fromJson(it.data.toString(), ChangeFoodStatusModel::class.java)
                            AppUtils.showToast(requireActivity(), it.data?.get("message").toString(), true)
                            getFeaturedFood(foodType)
                        }
                        ApiTags.BOOKMARK_THINGS -> {
                            val model = Gson().fromJson(it.data.toString(), InsightBookMarkModel::class.java)
                            AppUtils.showToast(requireActivity(), model.message, true)
                            if (model.message == "Bookmark added") {
                                if (selectedBookmark != -1)
                                    foodsList[selectedBookmark].is_bookmarked = 1
                            } else {
                                if (selectedBookmark != -1)
                                    foodsList[selectedBookmark].is_bookmarked = 0
                            }
                            binding.rvFindRecipes.adapter?.notifyDataSetChanged()
                        }

                    }
                }
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setAllFoods(model: AllFoodsModel) {
        if (isPullRefresh) {
            foodsList.clear()
        }
        //foodsList.clear()
        if (model.data.foods.isEmpty() && !isPullRefresh) {
            canLoadMore = false
        } else {
            canLoadMore = true
        }
        isPullRefresh = false
        foodsList.addAll(model.data.foods)
        findRecipesAdapter.notifyDataSetChanged()
    }

    @SuppressLint("SimpleDateFormat")
    private fun initViews() {
//        Boom(binding.btnDone)
//        Boom(binding.btnChange)
//        Boom(binding.btnSkip)
        Boom(binding.tvTabBreakFast)
        Boom(binding.tvTabLunch)
        Boom(binding.tvTabSnack)
        Boom(binding.tvTabDinner)
        val data = AppUtils.getUserDetails(requireContext())
        isGuest = data.is_guest
        foodType = "Breakfast"
        val formate = SimpleDateFormat("dd MMM, yyyy")
        binding.tvCompleteDate.text = formate.format(Calendar.getInstance().time)
        tabColorControl(0)
    }

    private fun initListener() {

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                //Toast.makeText(context,position.toString(), Toast.LENGTH_SHORT).show()
                if (position < topFoodList.size)
                    topFoodDetail = topFoodList[position]
            }

        })
        binding.tvTabBreakFast.setOnClickListener {
            if (!skeletonLayout.isSkeleton()) {
                foodType = "Breakfast"
                tabColorControl(0)
            }
        }
        binding.tvTabLunch.setOnClickListener {
            if (!skeletonLayout.isSkeleton()) {
                foodType = "Lunch"
                tabColorControl(1)
            }
        }
        binding.tvTabSnack.setOnClickListener {
            if (!skeletonLayout.isSkeleton()) {
                foodType = "Snack"
                tabColorControl(2)
            }
        }
        binding.tvTabDinner.setOnClickListener {
            if (!skeletonLayout.isSkeleton()) {
                foodType = "Dinner"
                tabColorControl(3)
            }
        }
        binding.tvTabBeverages.setOnClickListener {
            if (!skeletonLayout.isSkeleton()) {
                foodType = "Beverages"
                tabColorControl(4)
            }
        }
        binding.etSearch.setOnClickListener {
            AppUtils.preventDoubleClick(binding.etSearch)
            val intent = Intent(requireContext(), SimpleFoodSearchActivity::class.java)
            intent.putExtra("type", 0)
            startActivity(intent)
        }
        binding.pullToRefresh.setOnRefreshListener {
            isPullRefresh = true
            getFeaturedFood(foodType)
        }
    }

    private fun getAllFoods() {
        apiCall = retrofitClient.getAllFoods(skip, take)
        viewModel.getAllFoods(apiCall)
    }

    private fun bookmarkFood(id: Int) {
        apiCall = retrofitClient.bookmarkThings("foods", id)
        viewModel.bookmarkThings(apiCall)
    }

    private fun getFeaturedFood(type: String) {
        if (!isPullRefresh) {
            binding.llNoData.viewGone()
            binding.mainLayout.viewGone()
            //binding.skeletonLayoutScroll.viewVisible()
            binding.skeletonLayout.viewVisible()
            skeletonLayout.showSkeleton()
        }
        apiCall = retrofitClient.getFeaturedFoodByType(type)
        viewModel.getFeaturedFoodByType(apiCall)
    }

    private fun getFoodsByType(type: String) {
        if (!isPullRefresh) {
            binding.llNoData.viewGone()
            binding.mainLayout.viewGone()
            //binding.skeletonLayoutScroll.viewVisible()
            binding.skeletonLayout.viewVisible()
            skeletonLayout.showSkeleton()
        }
        apiCall = retrofitClient.getFoodByType(type)
        viewModel.getFoodByType(apiCall)
    }

    private fun initAdapters() {
        mAdapter = ViewSliderAdapter(topFoodList, this)
        binding.viewPager.adapter = mAdapter
        findRecipesAdapter = FindRecipesAdapter(requireContext(), foodsList, object : RecipeClickListener {
            override fun onItemClicked(position: Int, food_id: Int, option: ActivityOptionsCompat) {
                foodId = food_id.toString()
                val intent = Intent(mContext, RecipeDetailActivity::class.java)
                intent.putExtra("foodId", foodId)
                intent.putExtra("image", foodsList[position].image_path)
                intent.putExtra("time", foodsList[position].time)
                intent.putExtra("type", foodsList[position].type)
                intent.putExtra("calories", foodsList[position].calories)
                intent.putExtra("unit", foodsList[position].unit)
                intent.putExtra("title", foodsList[position].name)
                if (!foodsList[position].video.isNullOrEmpty())
                    intent.putExtra("videoPath", foodsList[position].video_path)
                intent.putExtra("isBookmarked", foodsList[position].is_bookmarked)
                activityLauncher.launch(intent, option, object : BaseActivityResult.OnActivityResult<ActivityResult> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onActivityResult(result: ActivityResult) {
                        if (result.resultCode == Activity.RESULT_OK) {
                            if (result.data != null) {
                                val isBookmarked = result.data?.getIntExtra("bookmark", -1)
                                if (isBookmarked != foodsList[position].is_bookmarked) {
                                    if (isBookmarked == 1)
                                        foodsList[position].is_bookmarked = 1
                                    else if (isBookmarked == 0)
                                        foodsList[position].is_bookmarked = 0
                                    binding.rvFindRecipes.adapter?.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                })
                //startActivity(intent, option.toBundle())
            }

            override fun onBookmarkClicked(position: Int) {
                if (isGuest == 0) {
                    selectedBookmark = position
                    bookmarkFood(foodsList[position].id)
                } else {
                    (activity as HomeActivity).showLoginDialog()
                }
            }
        })
        binding.rvFindRecipes.adapter = findRecipesAdapter

//        binding.rvFindRecipes.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                val lm = recyclerView.layoutManager as LinearLayoutManager
//                if (lm.findLastCompletelyVisibleItemPosition() == foodsList.size - 1)
//                    if (canLoadMore) {
//                        canLoadMore = false
//                        take += 10
//                        skip = foodsList.size
//                        getAllFoods()
//                    }
//            }
//        })
        binding.scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            Log.e("position", "${scrollY}---${v.getChildAt(0).measuredHeight - v.measuredHeight}")
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight + 47) {
                // end of the scroll view
                if (canLoadMore) {
                    canLoadMore = false
                    take += 10
                    skip = foodsList.size
                    getAllFoods()
                }
            }
        })
    }

    private fun tabColorControl(i: Int) {
        getFeaturedFood(foodType)

        binding.tvTabBreakFast.typeface = if (i == 0) ResourcesCompat.getFont(
            requireContext(),
            R.font.roboto_medium
        ) else ResourcesCompat.getFont(requireContext(), R.font.roboto_regular)
        binding.tvTabLunch.typeface = if (i == 1) ResourcesCompat.getFont(
            requireContext(),
            R.font.roboto_medium
        ) else ResourcesCompat.getFont(requireContext(), R.font.roboto_regular)
        binding.tvTabSnack.typeface = if (i == 2) ResourcesCompat.getFont(
            requireContext(),
            R.font.roboto_medium
        ) else ResourcesCompat.getFont(requireContext(), R.font.roboto_regular)
        binding.tvTabDinner.typeface = if (i == 3) ResourcesCompat.getFont(
            requireContext(),
            R.font.roboto_medium
        ) else ResourcesCompat.getFont(requireContext(), R.font.roboto_regular)
        binding.tvTabDinner.typeface = if (i == 4) ResourcesCompat.getFont(
            requireContext(),
            R.font.roboto_medium
        ) else ResourcesCompat.getFont(requireContext(), R.font.roboto_regular)
        binding.tvTabBreakFast.setTextColor(resources.getColor(if (i == 0) R.color.tab_selected else R.color.tab_unselected))
        binding.tvTabLunch.setTextColor(resources.getColor(if (i == 1) R.color.tab_selected else R.color.tab_unselected))
        binding.tvTabSnack.setTextColor(resources.getColor(if (i == 2) R.color.tab_selected else R.color.tab_unselected))
        binding.tvTabDinner.setTextColor(resources.getColor(if (i == 3) R.color.tab_selected else R.color.tab_unselected))
        binding.tvTabBeverages.setTextColor(resources.getColor(if (i == 4) R.color.tab_selected else R.color.tab_unselected))

    }

    private fun changeFoodStatus(id: Int, status: String) {
        binding.llNoData.viewGone()
        binding.mainLayout.viewGone()
        //binding.skeletonLayoutScroll.viewVisible()
        binding.skeletonLayout.viewVisible()
        skeletonLayout.showSkeleton()
        apiCall = retrofitClient.changeFoodStatus(id, status)
        viewModel.changeFoodStatus(apiCall)
    }

    private fun addRemoveFoodToUserFoods(id: Int, status: String) {
        binding.llNoData.viewGone()
        binding.mainLayout.viewGone()
        //binding.skeletonLayoutScroll.viewVisible()
        binding.skeletonLayout.viewVisible()
        skeletonLayout.showSkeleton()
        apiCall = retrofitClient.addRemoveFoodToUserFood(foodType, id, status)
        viewModel.addRemoveFoodToUserFoods(apiCall)
    }

    override fun onItemClick(view: View) {
        when (view.id) {
            R.id.ivFoodImage -> {
                if (topFoodDetail == null) {
                    return
                }

                foodId = topFoodDetail?.id.toString()
                val intent = Intent(mContext, RecipeDetailActivity::class.java)
                intent.putExtra("foodId", foodId)
                intent.putExtra("image", topFoodDetail?.image_path)
                intent.putExtra("time", topFoodDetail?.time)
                intent.putExtra("type", topFoodDetail?.type)
                intent.putExtra("calories", topFoodDetail?.calories)
                intent.putExtra("unit", topFoodDetail?.unit)
                intent.putExtra("title", topFoodDetail?.name)
                if (!topFoodDetail?.video.isNullOrEmpty())
                    intent.putExtra("videoPath", topFoodDetail?.video_path)
                intent.putExtra("isBookmarked", topFoodDetail?.is_bookmarked)
                val p1 = Pair.create<View, String>((view as View?)!!, "transitionImage")
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (mContext as AppCompatActivity),
                    p1
                )
                activityLauncher.launch(intent, options, object : BaseActivityResult.OnActivityResult<ActivityResult> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onActivityResult(result: ActivityResult) {
                        if (result.resultCode == Activity.RESULT_OK) {
                            if (result.data != null) {
                                val isBookmarked = result.data?.getIntExtra("bookmark", -1)
                                val id = result.data?.getIntExtra("id", -1)
                                if (isBookmarked != topFoodDetail?.is_bookmarked && id != -1) {
                                    for (item in foodsList) {
                                        if (item.id == id) {
                                            item.is_bookmarked = isBookmarked!!
                                            binding.rvFindRecipes.adapter?.notifyDataSetChanged()
                                            break
                                        }
                                    }
                                }
                            }
                        }
                    }
                })
                //startActivity(intent, options.toBundle())
            }
            R.id.btnDone -> {
                if (topFoodDetail != null)
                    changeFoodStatus(topFoodDetail?.id!!, "Done")
            }
            R.id.btnChange -> {
                val changeFoodBottomSheet =
                    ChangeFoodBottomSheet(foodType, bottomFoodList, object : ChangeFoodBottomSheet.ChangeFoodInterface {
                        override fun onNewFoodSelected(position: Int) {
                            topFoodDetail = bottomFoodList[position]
                            addRemoveFoodToUserFoods(bottomFoodList[position].id, "save")
                        }

                        override fun onRemoveFoodSelected(position: Int) {
                            if (bottomFoodList[position].food_status != "Done")
                                addRemoveFoodToUserFoods(bottomFoodList[position].id, "delete")
                        }
                    })

                changeFoodBottomSheet.show(requireActivity().supportFragmentManager, "ChangeFoodBottomSheet")
            }
            R.id.ivBookmark -> {
                if (topFoodDetail != null)
                    addRemoveFoodToUserFoods(topFoodDetail?.id!!, "delete")
            }
        }
    }

    inner class ViewSliderAdapter(val imagesList: MutableList<FoodsList>, val mListener: OnItemClickListener) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private lateinit var mContext: Context

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            mContext = parent.context
            return SliderViewHolder(
                DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_food_slider, parent, false), mListener
            )
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is SliderViewHolder)
                holder.onBind(position)
        }

        override fun getItemCount(): Int {
            if (imagesList.size == 0) {
                return 1
            } else {
                return imagesList.size
            }
        }

        inner class SliderViewHolder(val binding: ItemFoodSliderBinding, val listener: OnItemClickListener) : RecyclerView.ViewHolder(binding.root) {

            @SuppressLint("SetTextI18n")
            fun onBind(position: Int) {
                if (topFoodDetail == null) {
                    binding.ivBookmark.viewGone()
                    binding.llNoData.viewVisible()
                    binding.tvTitle.text = "No favourite recipe"
                    binding.tvTime.text = "0 min"
                    binding.tvCalories.text = "0 Calories"
                    Boom(binding.llAddFood)
                    binding.llAddFood.viewVisible()
                    binding.btnDone.viewGone()
                    binding.llChange.viewGone()
                    binding.llFeaturedFoodDone.viewGone()
                    if (isGuest == 1) {
                        binding.llFeaturedFoodBtn.viewGone()
                        binding.ivBookmark.viewGone()
                    }
                    binding.ivFoodImage.setImageResource(R.drawable.ic_food_place_holder)
                    binding.ivFoodImage.scaleType = ImageView.ScaleType.CENTER
                    binding.llAddFood.setOnClickListener {
                        AppUtils.preventDoubleClick(binding.llAddFood)
                        listener.onItemClick(binding.btnChange)
                    }
                } else {
                    binding.llNoData.viewGone()
                    binding.llAddFood.viewGone()
                    binding.ivBookmark.viewVisible()
                    binding.tvTitle.text = imagesList[position].name
                    binding.tvTime.text = "${imagesList[position].time} ${imagesList[position].unit}"
                    binding.tvCalories.text = "${imagesList[position].calories} Calories"
                    binding.ivFoodImage.scaleType = ImageView.ScaleType.CENTER_CROP
                    Glide.with(mContext)
                        .load(imagesList[position].image_path)
                        .into(binding.ivFoodImage)
                    Boom(binding.btnDone)
                    Boom(binding.ivBookmark)
                    Boom(binding.llChange)
                    Boom(binding.llFeaturedFoodDone)
                    if (imagesList[position].food_status != null && imagesList[position].food_status == "Done") {
                        binding.btnDone.viewGone()
                        binding.llFeaturedFoodDone.viewVisible()
                        binding.llChange.viewVisible()
                    } else {
                        binding.btnDone.viewVisible()
                        binding.llFeaturedFoodDone.viewGone()
                        binding.llChange.viewVisible()
                    }
                    if (isGuest == 1) {
                        binding.llFeaturedFoodBtn.viewGone()
                        binding.ivBookmark.viewGone()
                    }
                    binding.btnDone.setOnClickListener {
                        AppUtils.preventDoubleClick(binding.btnDone)
                        listener.onItemClick(binding.btnDone)
                    }
                    binding.btnChange.setOnClickListener {
                        AppUtils.preventDoubleClick(binding.btnChange)
                        listener.onItemClick(binding.btnChange)
                    }
                    binding.llAddFood.setOnClickListener {
                        AppUtils.preventDoubleClick(binding.llAddFood)
                        listener.onItemClick(binding.btnChange)
                    }
                    binding.llFeaturedFoodDone.setOnClickListener {
                        AppUtils.preventDoubleClick(binding.llFeaturedFoodDone)
                        listener.onItemClick(binding.ivFoodImage)
                    }
                    binding.ivFoodImage.setOnClickListener {
                        AppUtils.preventDoubleClick(binding.ivFoodImage)
                        listener.onItemClick(binding.ivFoodImage)
                    }
                    binding.ivBookmark.setOnClickListener {
                        AppUtils.preventDoubleClick(binding.ivBookmark)
                        listener.onItemClick(binding.ivBookmark)
                    }
                }
            }
        }

    }
}

interface OnItemClickListener {
    fun onItemClick(view: View)
}