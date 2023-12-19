package com.cp.brittany.dixon.activities.home.shops_tab

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.cart.CartActivity
import com.cp.brittany.dixon.activities.home.models.*
import com.cp.brittany.dixon.activities.home.notifications.NotificationsActivity
import com.cp.brittany.dixon.activities.home.product_page.ProductPageActivity
import com.cp.brittany.dixon.activities.home.shops_tab.adapters.*
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.base.AppController
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.FragmentShopBinding
import com.cp.brittany.dixon.loader.Loader
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.ApiStatus
import com.cp.brittany.dixon.utils.AppUtils
import com.cp.brittany.dixon.utils.viewGone
import com.cp.brittany.dixon.utils.viewVisible
import com.faltenreich.skeletonlayout.Skeleton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.transition.MaterialFadeThrough
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call

class ShopFragment : Fragment() {
    private lateinit var binding: FragmentShopBinding
    private lateinit var newArrivalsShopAdapter: NewArrivalsShopAdapter
    private lateinit var trendingShopAdapter: TrendingShopAdapter
    private lateinit var historyShopAdapter: HistoryShopAdapter
    private lateinit var featuredShopAdapter: FeaturedShopAdapter
    private lateinit var wishListAdapter: WishListAdapter
    private lateinit var baseActivity: BaseActivity

    private lateinit var mContext: Context
    private var wishList: MutableList<WishListData> = ArrayList()
    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>

    private var productSectionsList: MutableList<ProductSectionsData> = ArrayList()
    private var tabsCategoriesList: MutableList<ProductCategoriesData> = ArrayList()
    private var newArrivalProducts: MutableList<NewArrivalsProductsItem> = ArrayList()
    private var trendingProducts: MutableList<TrendingProductsItem> = ArrayList()
    private var historyProducts: MutableList<HistoryProductsItem> = ArrayList()
    private lateinit var skeletonLayout: Skeleton
    private var isPullRefresh = false
    private var skip = 0
    private var categoryId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentShopBinding.inflate(layoutInflater, container, false)
        skeletonLayout = binding.skeletonLayout
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        retrofitClient = RetrofitClient.getClient(requireContext()).create(Api::class.java)
        mContext = requireContext()
        baseActivity = BaseActivity()

        initViews()
        initVM()
        initListeners()
        initAdapters()
        setViewPager()
        observeApiResponse()

        getProductsSections()
    }

    private fun initViews() {
        binding.tvCartCount.text = AppController.cartCount.toString()
    }

    private fun initVM() {
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        binding.lifecycleOwner = this
    }

    private fun initListeners() {
        binding.ivNotification.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationsActivity::class.java))
        }
        binding.ivCart.setOnClickListener {
            startActivity(Intent(requireContext(), CartActivity::class.java))
        }
        binding.tvNewArrivals.setOnClickListener {
            val intent = Intent(requireContext(), NewArrivalDetailsActivity::class.java)
            intent.putExtra("id", categoryId)
            intent.putExtra("title", "New Arrivals")
            startActivity(intent)
        }
        binding.tvHistory.setOnClickListener {
            val intent = Intent(requireContext(), HistoryActivity::class.java)
            intent.putExtra("id", categoryId)
            intent.putExtra("title", "Viewed")
            startActivity(intent)
        }
        binding.tvWishList.setOnClickListener {
            val intent = Intent(requireContext(), HistoryActivity::class.java)
            intent.putExtra("isWishlist", true)
            intent.putExtra("title", "Wishlist")
            startActivity(intent)
        }
        binding.pullToRefresh.setOnRefreshListener {
            isPullRefresh = true
            //binding.pullToRefresh.isRefreshing = false
            getProductsSections()
        }
    }

    private fun setViewPager() {

    }

    private fun initAdapters() {
        /*New Arrival adapter*/
        newArrivalsShopAdapter = NewArrivalsShopAdapter(newArrivalProducts, object : NewArrivalsShopAdapter.NewArrivalProductInterface {
            override fun onItemClicked(position: Int, option: ActivityOptionsCompat) {
                val intent = Intent(mContext, ProductPageActivity::class.java)
                intent.putExtra("productId", newArrivalProducts[position].product_id)
                intent.putExtra("title", newArrivalProducts[position].product_name)
                intent.putExtra("price", newArrivalProducts[position].minimum_price.compare_at_price)
                intent.putExtra("discountedPrice", newArrivalProducts[position].minimum_price.price)
                if (newArrivalProducts[position].sub_category != null)
                    intent.putExtra("category", newArrivalProducts[position].sub_category.name)
                else
                    intent.putExtra("category", newArrivalProducts[position].categories.name)
                startActivity(intent, option.toBundle())
            }
        })
        binding.rvNewArrivals.adapter = newArrivalsShopAdapter

        /*Trending Adapter*/
        //val gridLayoutManager = GridLayoutManager(mContext, 3, RecyclerView.HORIZONTAL, false)
        //binding.rvTrending1.layoutManager = gridLayoutManager
        trendingShopAdapter = TrendingShopAdapter(trendingProducts, object : TrendingShopAdapter.TrendingProductsInterface {
            override fun onItemClicked(position: Int, option: ActivityOptionsCompat) {
                val intent = Intent(mContext, ProductPageActivity::class.java)
                intent.putExtra("productId", trendingProducts[position].product_data.product_id)
                intent.putExtra("title", trendingProducts[position].product_data.product_name)
                intent.putExtra("price", trendingProducts[position].product_data.minimum_price.compare_at_price)
                intent.putExtra("discountedPrice", trendingProducts[position].product_data.minimum_price.price)
                if (trendingProducts[position].product_data.sub_category != null)
                    intent.putExtra("category", trendingProducts[position].product_data.sub_category.name)
                else
                    intent.putExtra("category", trendingProducts[position].product_data.categories.name)
                startActivity(intent, option.toBundle())
            }
        })
        binding.rvTrending1.adapter = trendingShopAdapter

        /*History Adapter*/
        historyShopAdapter = HistoryShopAdapter(historyProducts, object : HistoryShopAdapter.HistoryProductsInterface {
            override fun onItemClicked(position: Int, option: ActivityOptionsCompat) {
                val intent = Intent(mContext, ProductPageActivity::class.java)
                intent.putExtra("productId", historyProducts[position].product_data.product_id)
                intent.putExtra("title", historyProducts[position].product_data.product_name)
                intent.putExtra("price", historyProducts[position].product_data.minimum_price.compare_at_price)
                intent.putExtra("discountedPrice", historyProducts[position].product_data.minimum_price.price)
                if (historyProducts[position].product_data.sub_category != null)
                    intent.putExtra("category", historyProducts[position].product_data.sub_category.name)
                else
                    intent.putExtra("category", historyProducts[position].product_data.categories.name)
                startActivity(intent, option.toBundle())
            }
        })
        binding.rvHistory.adapter = historyShopAdapter

        /*Featured Adapter*/
        featuredShopAdapter = FeaturedShopAdapter(productSectionsList, object : FeaturedShopAdapter.FeatureSectionsInterface {
            override fun onItemClicked(position: Int, option: ActivityOptionsCompat) {
                val intent = Intent(mContext, CollectionActivity::class.java)
                intent.putExtra("productId", productSectionsList[position].id)
                intent.putExtra("title", productSectionsList[position].name)
                startActivity(intent, option.toBundle())
            }
        })
        binding.rvTop.adapter = featuredShopAdapter

        wishList = ArrayList()
        wishListAdapter = WishListAdapter(wishList as ArrayList<WishListData>, true, object : WishListAdapter.WishListProductsInterface {
            override fun onItemClick(position: Int, option: ActivityOptionsCompat) {
                val intent = Intent(mContext, ProductPageActivity::class.java)
                intent.putExtra("productId", wishList[position].bookmarked_item.id)
                intent.putExtra("title", wishList[position].bookmarked_item.name)
                intent.putExtra("price", wishList[position].bookmarked_item.minimum_price.compare_at_price)
                intent.putExtra("discountedPrice", wishList[position].bookmarked_item.minimum_price.price)
                if (wishList[position].bookmarked_item.sub_category_name != null)
                    intent.putExtra("category", wishList[position].bookmarked_item.sub_category_name.name)
                else
                    intent.putExtra("category", wishList[position].bookmarked_item.category_name.name)
                startActivity(intent, option.toBundle())
            }

        })
        binding.rvWishList.adapter = wishListAdapter
    }

    private fun initTabs() {

        for (i in tabsCategoriesList.indices) {
            if (i == 0) {
                val tabTextView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_insight_category_tabs, null) as TextView
                tabTextView.text = tabsCategoriesList[i].name
                tabTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue_a700
                    )
                )
                tabTextView.typeface = baseActivity.fontMedium
                val tab = binding.shopCategoriesTab.newTab()
                tab.customView = tabTextView
                binding.shopCategoriesTab.addTab(tab)
            } else {
                val tabTextView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_insight_category_tabs, null) as TextView
                tabTextView.text = tabsCategoriesList[i].name
                val tab = binding.shopCategoriesTab.newTab()
                tab.customView = tabTextView
                binding.shopCategoriesTab.addTab(tab)
            }
        }
        binding.shopCategoriesTab.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val tabTextView = tab?.customView
                tabTextView?.findViewById<TextView>(R.id.tvTabTitle)
                    ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_a700))
                tabTextView?.findViewById<TextView>(R.id.tvTabTitle)?.typeface =
                    baseActivity.fontMedium

                categoryId = tabsCategoriesList[tab?.position!!].id
                getNewArrivals(categoryId)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val tabTextView = tab?.customView
                tabTextView?.findViewById<TextView>(R.id.tvTabTitle)
                    ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.tab_selected))
                tabTextView?.findViewById<TextView>(R.id.tvTabTitle)?.typeface =
                    baseActivity.fontMedium
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeApiResponse() {
        viewModel.getApiResponse().observe(requireActivity(), {
            when (it.status) {
                ApiStatus.LOADING -> {
//                    if (it.tag != ApiTags.GET_PRODUCT_CATEGORIES || it.tag != ApiTags.GET_NEW_ARRIVALS || it.tag != ApiTags.GET_TRENDING_PRODUCTS || it.tag != ApiTags.GET_HISTORY_PRODUCTS)
//                        Loader.showLoader((requireActivity() as AppCompatActivity)) {
//                            if (this@ShopFragment::apiCall.isInitialized)
//                                apiCall.cancel()
//                        }
                }
                ApiStatus.ERROR -> {
                    //Loader.hideLoader()
                    AppUtils.showToast(requireActivity(), it.message!!, false)
                }
                ApiStatus.SUCCESS -> {
                    when (it.tag) {
                        ApiTags.GET_PRODUCTS_SECTIONS -> {

                            val model = Gson().fromJson(it.data.toString(), ProductSectionsModel::class.java)
                            productSectionsList.clear()
                            productSectionsList.addAll(model.data)
                            featuredShopAdapter.notifyDataSetChanged()
                            getProductCategories()
                        }
                        ApiTags.GET_PRODUCT_CATEGORIES -> {


                            val model = Gson().fromJson(it.data.toString(), ProductCategoriesModel::class.java)
                            tabsCategoriesList.clear()
                            tabsCategoriesList.add(ProductCategoriesData(name = "ALL"))
                            if (model.data == null) {
                                binding.mainLayout.viewVisible()
                                binding.skeletonLayoutScroll.viewGone()
                                binding.skeletonLayout.viewGone()
                                binding.tvNewArrivals.viewGone()
                                binding.ivForwardEssentials.viewGone()
                                binding.viewBelowNewArrivals.viewGone()
                                binding.tvWishList.viewGone()
                                binding.ivWishList.viewGone()
                                binding.viewBelowWishList.viewGone()
                                binding.tvHistory.viewGone()
                                binding.ivHistory.viewGone()
                                binding.rvNewArrivals.viewGone()
                                binding.rvWishList.viewGone()
                                binding.rvTrending1.viewGone()
                                binding.rvHistory.viewGone()
                                skeletonLayout.showOriginal()
                                binding.pullToRefresh.isRefreshing = false
                                isPullRefresh = false
                                if (binding.shopCategoriesTab.tabCount == 0) {
                                    initTabs()
                                }
                                return@observe
                            }
                            tabsCategoriesList.addAll(model.data)
                            if (binding.shopCategoriesTab.tabCount == 0) {
                                initTabs()
                            }
                            getNewArrivals(categoryId)
                        }
                        ApiTags.GET_NEW_ARRIVALS -> {


                            val model = Gson().fromJson(it.data.toString(), NewArrivalsProductsModel::class.java)
                            newArrivalProducts.clear()
                            newArrivalProducts.addAll(model.data.items)
                            getWishListProducts()
                            //getTrendingProduct(categoryId)

                            if (newArrivalProducts.isNotEmpty()) {
                                binding.tvNewArrivals.viewVisible()
                                binding.ivForwardEssentials.viewVisible()
                                binding.viewBelowNewArrivals.viewVisible()
                                binding.rvNewArrivals.viewVisible()
                                newArrivalsShopAdapter.notifyDataSetChanged()
                            } else {
                                binding.tvNewArrivals.viewGone()
                                binding.ivForwardEssentials.viewGone()
                                binding.viewBelowNewArrivals.viewGone()
                                binding.rvNewArrivals.viewGone()
                            }

                        }
                        ApiTags.GET_BOOKMARK -> {
                            wishList.clear()
                            val model = Gson().fromJson(it.data.toString(), WishListModel::class.java)
                            wishList.addAll(model.data)
                            getTrendingProduct(categoryId)
                            if (wishList.isNotEmpty()) {
                                binding.tvWishList.viewVisible()
                                binding.ivWishList.viewVisible()
                                binding.viewBelowWishList.viewVisible()
                                binding.rvWishList.viewVisible()
                                wishListAdapter.notifyDataSetChanged()
                            } else {
                                binding.tvWishList.viewGone()
                                binding.ivWishList.viewGone()
                                binding.viewBelowWishList.viewGone()
                                binding.rvWishList.viewGone()
                            }

                        }
                        ApiTags.GET_TRENDING_PRODUCTS -> {

                            val model = Gson().fromJson(it.data.toString(), TrendingProductsModel::class.java)
                            trendingProducts.clear()
                            trendingProducts.addAll(model.data.items)

                            if (trendingProducts.isNotEmpty()) {
                                binding.tvTrending.viewVisible()
                                //binding.ivTrending.viewVisible()
                                binding.viewBelowTrending1.viewVisible()
                                binding.rvTrending1.viewVisible()
                                trendingShopAdapter.notifyDataSetChanged()
                            } else {
                                binding.tvTrending.viewGone()
                                binding.ivTrending.viewGone()
                                binding.rvTrending1.viewGone()
                                binding.viewBelowTrending1.viewGone()
                            }

                            getHistoryProducts(categoryId)
                        }
                        ApiTags.GET_HISTORY_PRODUCTS -> {
                            binding.mainLayout.viewVisible()
                            binding.skeletonLayoutScroll.viewGone()
                            binding.skeletonLayout.viewGone()
                            skeletonLayout.showOriginal()
                            binding.pullToRefresh.isRefreshing = false
                            isPullRefresh = false

                            Loader.hideLoader()
                            val model = Gson().fromJson(it.data.toString(), HistoryProductsModel::class.java)
                            historyProducts.clear()
                            historyProducts.addAll(model.data.items)

                            if (historyProducts.isNotEmpty()) {
                                binding.tvHistory.viewVisible()
                                binding.ivHistory.viewVisible()
                                binding.rvHistory.viewVisible()
                                historyShopAdapter.notifyDataSetChanged()
                            } else {
                                binding.tvHistory.viewGone()
                                binding.ivHistory.viewGone()
                                binding.rvHistory.viewGone()
                            }
                            if (productSectionsList.size > 0) {
                                binding.rvTop.scrollToPosition(0)
                            }
                            if (newArrivalProducts.size > 0) {
                                binding.rvNewArrivals.scrollToPosition(0)
                            }
                            if (historyProducts.size > 0) {
                                binding.rvHistory.scrollToPosition(0)
                            }
                        }
                    }
                }
            }
        })
    }

    private fun getProductsSections() {
        if (!isPullRefresh) {
            binding.mainLayout.viewGone()
            binding.skeletonLayoutScroll.viewVisible()
            binding.skeletonLayout.viewVisible()
            skeletonLayout.showSkeleton()
        }
        apiCall = retrofitClient.getProductSections()
        viewModel.getProductSections(apiCall)
    }

    private fun getProductCategories() {
        apiCall = retrofitClient.getProductCategories(skip)
        viewModel.getProductCategories(apiCall)
    }

    private fun getNewArrivals(id: Int) {
        apiCall = retrofitClient.getNewArrivals(id)
        viewModel.getNewArrivals(apiCall)
    }

    private fun getTrendingProduct(id: Int) {
        apiCall = retrofitClient.getTrendingProduct(id)
        viewModel.getTrendingProduct(apiCall)
    }

    private fun getHistoryProducts(id: Int) {
        apiCall = retrofitClient.getHistoryProducts(id, skip)
        viewModel.getHistoryProducts(apiCall)
    }

    private fun getWishListProducts() {
        apiCall = retrofitClient.getWorkoutBookmark("products")
        viewModel.getWorkoutBookmarks(apiCall)
    }

    override fun onResume() {
        super.onResume()
        binding.tvCartCount.text = AppController.cartCount.toString()
    }

}