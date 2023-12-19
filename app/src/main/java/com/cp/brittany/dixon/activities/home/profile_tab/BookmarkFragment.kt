package com.cp.brittany.dixon.activities.home.profile_tab

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModelProviders
import com.cp.brittany.dixon.activities.home.food_tab.RecipeDetailActivity
import com.cp.brittany.dixon.activities.home.insight_rec_detail.InsightRecommendationDetailActivity
import com.cp.brittany.dixon.activities.home.models.BookmarkModel
import com.cp.brittany.dixon.activities.home.models.BookmarkModelData
import com.cp.brittany.dixon.activities.home.product_page.ProductPageActivity
import com.cp.brittany.dixon.activities.home.profile_tab.adapters.*
import com.cp.brittany.dixon.activities.home.shops_tab.HistoryActivity
import com.cp.brittany.dixon.activities.home.workout_detail.WorkoutDetailActivity
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.databinding.FragmentBookmarkBinding
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
import com.cp.brittany.dixon.activities.home.models.MessageEvent
import com.cp.brittany.dixon.base.BaseActivityResult

import com.werb.eventbus.DEFAULT_TAG
import com.werb.eventbus.EventBus
import com.werb.eventbus.Subscriber
import com.werb.eventbus.ThreadMode
import org.json.JSONException


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BookmarkFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BookmarkFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    val activityLauncher: BaseActivityResult<Intent, ActivityResult> =
        BaseActivityResult.registerActivityForResult(this)
    private lateinit var binding: FragmentBookmarkBinding
    private lateinit var bookmark: BookmarkModelData
    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBookmarkBinding.inflate(layoutInflater, container, false)
        retrofitClient = RetrofitClient.getClient(requireContext()).create(Api::class.java)
        initVM()
        initListeners()
        getWorkoutBookmarks()
        observeApiResponse()
        return binding.root
    }

    private fun initListeners() {
        binding.pullToRefresh.setOnRefreshListener {
            //binding.pullToRefresh.isRefreshing = false
            getWorkoutBookmarks()
        }
        binding.ivShopForward.setOnClickListener {
            AppUtils.preventDoubleClick(binding.ivShopForward)
            val intent = Intent(requireContext(), HistoryActivity::class.java)
            intent.putExtra("isWishlist", true)
            intent.putExtra("title", "Wishlist")
            startActivity(intent)
        }
        binding.ivFoodForward.setOnClickListener {
            AppUtils.preventDoubleClick(binding.ivFoodForward)
            val intent = Intent(requireContext(), BookmarkDetailsActivity::class.java)
            intent.putExtra("type", TYPE_FOOD)
            activityLauncher.launch(intent, object : BaseActivityResult.OnActivityResult<ActivityResult> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onActivityResult(result: ActivityResult) {
                    if (result.resultCode == Activity.RESULT_OK) {
                        if (result.data != null && result.data!!.hasExtra("size")) {
                            val size = result.data?.getIntExtra("size", -1)
                            if (size != bookmark.foods.size) {
                                binding.pullToRefresh.isRefreshing = true
                                getWorkoutBookmarks()
                            }
                        }
                    }
                }
            })
        }
        binding.ivInsightForward.setOnClickListener {
            AppUtils.preventDoubleClick(binding.ivInsightForward)
            val intent = Intent(requireContext(), BookmarkDetailsActivity::class.java)
            intent.putExtra("type", TYPE_INSIGHT)
            activityLauncher.launch(intent, object : BaseActivityResult.OnActivityResult<ActivityResult> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onActivityResult(result: ActivityResult) {
                    if (result.resultCode == Activity.RESULT_OK) {
                        if (result.data != null && result.data!!.hasExtra("size")) {
                            val size = result.data?.getIntExtra("size", -1)
                            if (size != bookmark.insights.size) {
                                binding.pullToRefresh.isRefreshing = true
                                getWorkoutBookmarks()
                            }
                        }
                    }
                }
            })
        }
        binding.ivWorkoutForward.setOnClickListener {
            AppUtils.preventDoubleClick(binding.ivWorkoutForward)
            val intent = Intent(requireContext(), BookmarkDetailsActivity::class.java)
            intent.putExtra("type", TYPE_WORKOUT)
            activityLauncher.launch(intent, object : BaseActivityResult.OnActivityResult<ActivityResult> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onActivityResult(result: ActivityResult) {
                    if (result.resultCode == Activity.RESULT_OK) {
                        if (result.data != null && result.data!!.hasExtra("size")) {
                            val size = result.data?.getIntExtra("size", -1)
                            if (size != bookmark.workouts.size) {
                                binding.pullToRefresh.isRefreshing = true
                                getWorkoutBookmarks()
                            }
                        }
                    }
                }
            })
        }
    }

    private fun initAdapter() {
        if (bookmark.workouts != null) {
            binding.workoutLayout.viewVisible()
            binding.rvWorkout.adapter = BookmarksInnerAdapter(bookmark, TYPE_WORKOUT, object : BookmarksInnerAdapter.BookmarksClickListener {
                override fun onItemClick(position: Int, option: ActivityOptionsCompat) {
                    val i = Intent(requireContext(), WorkoutDetailActivity::class.java)
                    i.putExtra("workoutId", bookmark.workouts[position].bookmarked_item.id.toString())
                    i.putExtra("workoutImage", bookmark.workouts[position].bookmarked_item.image_path)
                    i.putExtra("title", bookmark.workouts[position].bookmarked_item.name)
                    i.putExtra("details", bookmark.workouts[position].bookmarked_item.description)
                    i.putExtra("weight", bookmark.workouts[position].bookmarked_item.weight_status)
                    i.putExtra("level", bookmark.workouts[position].bookmarked_item.level)
                    i.putExtra("percentageCompleted", bookmark.workouts[position].bookmarked_item.percentage_completed)
                    i.putExtra("isBookmarked", bookmark.workouts[position].bookmarked_item.is_bookmarked)
                    i.putExtra("time", bookmark.workouts[position].bookmarked_item.duration)
                    i.putExtra("unit", bookmark.workouts[position].bookmarked_item.unit)
                    i.putExtra("is_free", bookmark.workouts[position].bookmarked_item.is_free)
                    i.putExtra("calories", bookmark.workouts[position].bookmarked_item.cal_gain_reduce)
                    activityLauncher.launch(i, option, object : BaseActivityResult.OnActivityResult<ActivityResult> {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onActivityResult(result: ActivityResult) {
                            if (result.resultCode == Activity.RESULT_OK) {
                                if (result.data != null && result.data?.hasExtra("bookmark") == true) {
                                    val isBookmarked = result.data?.getIntExtra("bookmark", -1)
                                    if (isBookmarked != bookmark.workouts[position].bookmarked_item.is_bookmarked) {
                                        if (isBookmarked == 0) {
                                            bookmark.workouts.removeAt(position)
                                            binding.rvWorkout.adapter?.notifyDataSetChanged()
                                            validateLayout()
                                        }
                                    }
                                }
                            }
                        }
                    })
                }

            })
        } else
            binding.workoutLayout.viewGone()
        if (bookmark.products != null) {
            binding.shopLayout.viewVisible()
            binding.rvShop.adapter = BookmarksInnerAdapter(bookmark, TYPE_SHOP, object : BookmarksInnerAdapter.BookmarksClickListener {
                override fun onItemClick(position: Int, option: ActivityOptionsCompat) {
                    val intent = Intent(requireContext(), ProductPageActivity::class.java)
                    intent.putExtra("productId", bookmark.products[position].bookmarked_item.id)
                    intent.putExtra("title", bookmark.products[position].bookmarked_item.name)
                    intent.putExtra("price", bookmark.products[position].bookmarked_item.minimum_price.compare_at_price)
                    intent.putExtra("discountedPrice", bookmark.products[position].bookmarked_item.minimum_price.price)
                    if (bookmark.products[position].bookmarked_item.sub_category_name != null)
                        intent.putExtra("category", bookmark.products[position].bookmarked_item.sub_category_name.name)
                    else
                        intent.putExtra("category", bookmark.products[position].bookmarked_item.category_name.name)
                    startActivity(intent, option.toBundle())

                }

            })
        } else
            binding.shopLayout.viewGone()
        if (bookmark.foods != null) {
            binding.foodLayout.viewVisible()
            binding.rvFood.adapter = BookmarksInnerAdapter(bookmark, TYPE_FOOD, object : BookmarksInnerAdapter.BookmarksClickListener {
                override fun onItemClick(position: Int, option: ActivityOptionsCompat) {
                    val intent = Intent(requireContext(), RecipeDetailActivity::class.java)
                    intent.putExtra("foodId", bookmark.foods[position].bookmarked_item.id.toString())
                    intent.putExtra("image", bookmark.foods[position].bookmarked_item.image_path)
                    intent.putExtra("time", bookmark.foods[position].bookmarked_item.time)
                    intent.putExtra("type", bookmark.foods[position].bookmarked_item.type)
                    intent.putExtra("calories", bookmark.foods[position].bookmarked_item.calories)
                    intent.putExtra("unit", bookmark.foods[position].bookmarked_item.unit)
                    intent.putExtra("title", bookmark.foods[position].bookmarked_item.name)
                    intent.putExtra("isBookmarked", bookmark.foods[position].bookmarked_item.is_bookmarked)
                    if (!bookmark.foods[position].bookmarked_item.video.isNullOrEmpty())
                        intent.putExtra("videoPath", bookmark.foods[position].bookmarked_item.video_path)
                    activityLauncher.launch(intent, option, object : BaseActivityResult.OnActivityResult<ActivityResult> {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onActivityResult(result: ActivityResult) {
                            if (result.resultCode == Activity.RESULT_OK) {
                                if (result.data != null) {
                                    val isBookmarked = result.data?.getIntExtra("bookmark", -1)
                                    if (isBookmarked != bookmark.foods[position].bookmarked_item.is_bookmarked) {
                                        if (isBookmarked == 0) {
                                            bookmark.foods.removeAt(position)
                                            binding.rvFood.adapter?.notifyDataSetChanged()
                                            validateLayout()
                                        }
                                    }
                                }
                            }
                        }
                    })
                }

            })
        } else
            binding.foodLayout.viewGone()
        if (bookmark.insights != null) {
            binding.insightLayout.viewVisible()
            binding.rvInsight.adapter = BookmarksInnerAdapter(bookmark, TYPE_INSIGHT, object : BookmarksInnerAdapter.BookmarksClickListener {
                override fun onItemClick(position: Int, option: ActivityOptionsCompat) {
                    val intent = Intent(requireContext(), InsightRecommendationDetailActivity::class.java)
                    intent.putExtra("isFromBookmark", true)
                    intent.putExtra("id", bookmark.insights[position].bookmarked_item.id)
                    intent.putExtra("insightImage", bookmark.insights[position].bookmarked_item.image_path)
                    intent.putExtra("time", bookmark.insights[position].bookmarked_item.duration)
                    intent.putExtra("unit", bookmark.insights[position].bookmarked_item.unit)
                    intent.putExtra("title", bookmark.insights[position].bookmarked_item.name)
                    intent.putExtra("date", bookmark.insights[position].bookmarked_item.date)
                    intent.putExtra("isBookmarked", bookmark.insights[position].bookmarked_item.is_bookmarked)
                    intent.putExtra("details", bookmark.insights[position].bookmarked_item.detail)
                    intent.putExtra("totalLikes", bookmark.insights[position].bookmarked_item.total_likes)
                    intent.putExtra("isLiked", bookmark.insights[position].bookmarked_item.is_liked)
                    intent.putExtra("created_at", bookmark.insights[position].bookmarked_item.created_at)
                    activityLauncher.launch(intent, option, object : BaseActivityResult.OnActivityResult<ActivityResult> {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onActivityResult(result: ActivityResult) {
                            if (result.resultCode == Activity.RESULT_OK) {
                                if (result.data != null) {
                                    val isBookmarked = result.data?.getIntExtra("bookmark", -1)
                                    if (isBookmarked != bookmark.insights[position].bookmarked_item.is_bookmarked) {
                                        if (isBookmarked == 0) {
                                            bookmark.insights.removeAt(position)
                                            binding.rvInsight.adapter?.notifyDataSetChanged()
                                            validateLayout()
                                        }
                                    }
                                }
                            }
                        }
                    })
                }

            })
        } else
            binding.insightLayout.viewGone()
    }

    private fun validateLayout() {
        if (bookmark.foods.isNullOrEmpty()) {
            binding.foodLayout.viewGone()
        }
        if (bookmark.workouts.isNullOrEmpty()) {
            binding.workoutLayout.viewGone()
        }
        if (bookmark.insights.isNullOrEmpty()) {
            binding.insightLayout.viewGone()
        }
        if (bookmark.foods.isNullOrEmpty() && bookmark.workouts.isNullOrEmpty() && bookmark.insights.isNullOrEmpty()) {
            binding.noData.viewVisible()
        }
    }

    private fun getWorkoutBookmarks() {
        apiCall = retrofitClient.getWorkoutBookmark("all")
        viewModel.getWorkoutBookmarks(apiCall)
    }

    private fun initVM() {
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        binding.lifecycleOwner = this
    }

    private fun observeApiResponse() {
        viewModel.getApiResponse().observe(requireActivity(), {
            when (it.status) {
                ApiStatus.LOADING -> {
//                    Loader.showLoader((requireActivity() as AppCompatActivity)) {
//                        if (this::apiCall.isInitialized)
//                            apiCall.cancel()
//                    }
                }
                ApiStatus.ERROR -> {
                    //Loader.hideLoader()
                    binding.pullToRefresh.isRefreshing = false
                    AppUtils.showToast(requireActivity(), it.message!!, false)
                }
                ApiStatus.SUCCESS -> {
                    //Loader.hideLoader()
                    when (it.tag) {
                        ApiTags.GET_BOOKMARK -> {
                            binding.pullToRefresh.isRefreshing = false

                            try {
                                val model = Gson().fromJson(it.data.toString(), BookmarkModel::class.java)
                                binding.noData.viewGone()
                                setBookMarks(model)
                            } catch (ex: IllegalStateException) {
                                binding.workoutLayout.viewGone()
                                binding.shopLayout.viewGone()
                                binding.foodLayout.viewGone()
                                binding.insightLayout.viewGone()
                                binding.noData.viewVisible()
                            } catch (ex: Exception) {
                                binding.workoutLayout.viewGone()
                                binding.shopLayout.viewGone()
                                binding.foodLayout.viewGone()
                                binding.insightLayout.viewGone()
                                binding.noData.viewVisible()
                            }
                        }
                    }
                }
            }
        })
    }

    private fun setBookMarks(model: BookmarkModel) {
        if (model != null && model.data != null) {
            //binding.noData.visibility = View.GONE
            bookmark = model.data
            initAdapter()
        } else {
            binding.workoutLayout.viewGone()
            binding.shopLayout.viewGone()
            binding.foodLayout.viewGone()
            binding.insightLayout.viewGone()

            //binding.noData.visibility = View.VISIBLE

        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BookmarkFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BookmarkFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}