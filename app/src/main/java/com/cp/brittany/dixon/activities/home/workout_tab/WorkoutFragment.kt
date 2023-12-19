package com.cp.brittany.dixon.activities.home.workout_tab

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.auth.models.BannerData
import com.cp.brittany.dixon.activities.auth.models.BannerModel
import com.cp.brittany.dixon.activities.home.HomeActivity
import com.cp.brittany.dixon.activities.home.cart.CartActivity
import com.cp.brittany.dixon.activities.home.home_models.AllWorkoutCategory
import com.cp.brittany.dixon.activities.home.home_models.AllWorkoutModel
import com.cp.brittany.dixon.activities.home.models.CartModel
import com.cp.brittany.dixon.activities.home.notifications.NotificationsActivity
import com.cp.brittany.dixon.activities.home.premium.PremiumActivity
import com.cp.brittany.dixon.activities.home.search.SearchActivity
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.base.AppController
import com.cp.brittany.dixon.databinding.FragmentWorkoutBinding
import com.cp.brittany.dixon.databinding.ItemProductImagesBinding
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.ApiStatus
import com.cp.brittany.dixon.utils.AppUtils
import com.cp.brittany.dixon.utils.viewGone
import com.cp.brittany.dixon.utils.viewVisible
import com.faltenreich.skeletonlayout.Skeleton
import com.google.android.material.transition.MaterialFadeThrough
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call


class WorkoutFragment : Fragment() {
    private lateinit var binding: FragmentWorkoutBinding
    private lateinit var adapter: WorkoutFragmentMainAdapter
    private var imagesList: ArrayList<BannerData> = ArrayList()
    private var mAdapter: ViewsSliderAdapter? = null
    private lateinit var mContext: Context
    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>

    private var workoutList: MutableList<AllWorkoutCategory?> = ArrayList()

    private lateinit var vpf: ViewPagerFragmentAdapter
    private lateinit var runnable: Runnable
    private lateinit var handler: Handler
    private val delay = 3000
    private var page = 0
    private lateinit var skeletonLayout: Skeleton
    private var isPullRefresh = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentWorkoutBinding.inflate(layoutInflater, container, false)
        skeletonLayout = binding.skeletonLayout
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as HomeActivity).changeBarColor(R.color.white)
        mContext = requireContext()

        retrofitClient = RetrofitClient.getClient(mContext).create(Api::class.java)
        initVM()
        initVar()
        listener()
        setAdapter()
        getBanner()
        observeApiResponse()
//        checkForWearable()
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
                    //if (it.tag != ApiTags.GET_CART_ITEMS)
                    //Loader.showLoader((requireActivity() as AppCompatActivity)) {
                    //if (this@WorkoutFragment::apiCall.isInitialized)
                    //  apiCall.cancel()
                    //}
                }
                ApiStatus.ERROR -> {
                    //Loader.hideLoader()
                    AppUtils.showToast(requireActivity(), it.message!!, false)
                }
                ApiStatus.SUCCESS -> {
                    //Loader.hideLoader()
                    when (it.tag) {
                        ApiTags.GET_ALL_WORKOUTS -> {
                            binding.scrollView.viewVisible()
                            binding.skeletonLayout.viewGone()
                            binding.skeletonLayoutScroll.viewGone()
                            skeletonLayout.showOriginal()
                            binding.pullToRefresh.isRefreshing = false
                            try {
                                val model =
                                    Gson().fromJson(it.data.toString(), AllWorkoutModel::class.java)
                                setWorkouts(model.data.categories)
                            } catch (ex: Exception) {

                            }

                            //getCartItems()
                        }
                        ApiTags.GET_CART_ITEMS -> {
                            binding.scrollView.viewVisible()
                            binding.skeletonLayout.viewGone()
                            binding.skeletonLayoutScroll.viewGone()
                            skeletonLayout.showOriginal()
                            binding.pullToRefresh.isRefreshing = false

                            val model = Gson().fromJson(it.data.toString(), CartModel::class.java)
                            AppController.cartCount = model.data.items.size
                            binding.tvCartCount.text = AppController.cartCount.toString()
                        }
                        ApiTags.GET_BANNER -> {
                            val model = Gson().fromJson(it.data.toString(), BannerModel::class.java)
                            imagesList.addAll(model.data)
                            mAdapter = ViewsSliderAdapter(imagesList)
                            binding.viewPager.adapter = mAdapter
                            binding.dotsIndicator.attachToPager(binding.viewPager)
                            mAdapter?.notifyDataSetChanged()
                            getAllWorkouts()
                        }
                    }

                }
            }
        })
    }

    private fun getBanner() {
        if (!isPullRefresh) {
            binding.scrollView.viewGone()
            binding.skeletonLayoutScroll.viewVisible()
            binding.skeletonLayout.viewVisible()
            skeletonLayout.showSkeleton()
        }
        apiCall = retrofitClient.getBanner("home_page")
        viewModel.getBanner(apiCall)
    }

    private fun setWorkouts(list: List<AllWorkoutCategory>) {
        workoutList.clear()
        workoutList.addAll(list)
        if (workoutList.size >= 3)
            workoutList.add(2, AllWorkoutCategory(-1, "", "", "", "", -1, ArrayList(), true))
        else
            workoutList.add(AllWorkoutCategory(-1, "", "", "", "", -1, ArrayList(), true))
        adapter.setItems(workoutList)
    }

    private fun initVar() {


        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
//                if (vpf.itemCount == page) {
//                    page = 0
//                } else {
//                    page++
//                }
//                binding.viewPager.setCurrentItem(page, true)
//                handler.postDelayed(this, delay.toLong())
            }
        }
    }

//    private fun setViewPager() {
//        vpf = ViewPagerFragmentAdapter(requireContext() as FragmentActivity)
//        binding.viewPager.adapter = vpf
//        binding.viewPager.registerOnPageChangeCallback(pageChangeCallback)
//    }

    private var pageChangeCallback: ViewPager2.OnPageChangeCallback =
        object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                page = position
            }
        }

    private fun setAdapter() {
        adapter = WorkoutFragmentMainAdapter(requireContext(), workoutList, object : WorkoutListener {
            override fun onItemClicked(position: Int, workoutId: Int) {
//                val intent = Intent(mContext, WorkoutDetailActivity::class.java)
//                intent.putExtra("workoutId", workoutId.toString())
//                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                    requireActivity(),
//                    (ivWork as View?)!!, "profile"
//                )
//                startActivity(intent, options.toBundle())
            }

            override fun onPremiumClicked() {
                startActivity(Intent(mContext, PremiumActivity::class.java))
            }

        })
        binding.rvAllWorkouts.setHasFixedSize(true)
        binding.rvAllWorkouts.adapter = adapter
    }

    private fun getAllWorkouts() {
        if (!isPullRefresh) {
            binding.scrollView.viewGone()
            binding.skeletonLayoutScroll.viewVisible()
            binding.skeletonLayout.viewVisible()
            skeletonLayout.showSkeleton()
        }
        apiCall = retrofitClient.getAllWorkouts()
        viewModel.getAllWorkouts(apiCall)
    }

    private fun getCartItems() {
        if (!isPullRefresh) {
            binding.scrollView.viewGone()
            binding.skeletonLayoutScroll.viewVisible()
            binding.skeletonLayout.viewVisible()
            skeletonLayout.showSkeleton()
        }
        apiCall = retrofitClient.getCartItems()
        viewModel.getCartItems(apiCall)
    }

    private fun listener() {
        binding.ivCart.setOnClickListener {
            AppUtils.preventDoubleClick(binding.ivCart)
            startActivity(Intent(requireContext(), CartActivity::class.java))
        }
        binding.ivNotification.setOnClickListener {
            AppUtils.preventDoubleClick(binding.ivNotification)
            startActivity(Intent(requireContext(), NotificationsActivity::class.java))
        }
        binding.ivSearch.setOnClickListener {
            AppUtils.preventDoubleClick(binding.ivSearch)
            startActivity(Intent(requireContext(), SearchActivity::class.java))
        }
        binding.pullToRefresh.setOnRefreshListener {
            isPullRefresh = true
            //binding.pullToRefresh.isRefreshing = false
            getAllWorkouts()
        }
    }

    private class ViewPagerFragmentAdapter(
        fragmentActivity: FragmentActivity
    ) :
        FragmentStateAdapter(fragmentActivity) {
        override fun createFragment(position: Int): Fragment {

            val image1 = FeaturedImage1Fragment()

            when (position) {
                0 -> return image1
                1 -> return image1
                2 -> return image1
                3 -> return image1
                4 -> return image1
            }
            return image1
        }

        override fun getItemCount(): Int {
            return 5
        }
    }

    override fun onResume() {
        super.onResume()
        binding.tvCartCount.text = AppController.cartCount.toString()
        handler.postDelayed(runnable, delay.toLong())
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    private fun checkForWearable() {
        try {
            activity?.packageManager?.getPackageInfo("com.google.android.wearable.app", PackageManager.GET_META_DATA)
            Toast.makeText(mContext, "The Android Wear App is installed", Toast.LENGTH_SHORT).show()
        } catch (e: PackageManager.NameNotFoundException) {
            //android wear app is not installed
            Toast.makeText(mContext, "The Android Wear App is NOT installed", Toast.LENGTH_SHORT).show()
        }
    }


    inner class ViewsSliderAdapter(val imagesList: MutableList<BannerData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private lateinit var mContext: Context

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            mContext = parent.context
            return SliderViewHolder(
                DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_product_images, parent, false)
            )
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is SliderViewHolder)
                holder.onBind(position)
        }

        override fun getItemCount(): Int {
            return imagesList.size
        }

        inner class SliderViewHolder(val binding: ItemProductImagesBinding) : RecyclerView.ViewHolder(binding.root) {

            fun onBind(position: Int) {

                Glide.with(mContext)
                    .load(imagesList[position].image_path)
                    .placeholder(R.drawable.ic_product_placeholder)
                    .into(binding.ivProductImage)
            }
        }
    }


}