package com.cp.brittany.dixon.activities.home.bookmarks.posts_tab

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProviders
import com.cp.brittany.dixon.activities.home.bookmarks.BookmarksActivity
import com.cp.brittany.dixon.activities.home.insight_rec_detail.InsightRecommendationDetailActivity
import com.cp.brittany.dixon.activities.home.models.BookMarkData
import com.cp.brittany.dixon.activities.home.models.BookMarksModel
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.base.BaseActivityResult
import com.cp.brittany.dixon.databinding.FragmentBookmarksPostsBinding
import com.cp.brittany.dixon.loader.Loader
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.ApiStatus
import com.cp.brittany.dixon.utils.AppUtils
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call

class BookmarksPostsFragment : Fragment() {
    private lateinit var binding: FragmentBookmarksPostsBinding
    private lateinit var adapter: BookmarksPostsAdapter
    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>
    private lateinit var mContext: Context
    private var bookmarkList: MutableList<BookMarkData> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBookmarksPostsBinding.inflate(layoutInflater, container, false)
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
        initAdapters()
        getInsightBookmarks()
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

    }

    private fun initAdapters() {
        adapter = BookmarksPostsAdapter(bookmarkList, object : BookmarksPostsAdapter.BookmarkInterface {
            override fun onItemClicked(position: Int, options: ActivityOptionsCompat) {
                val i = Intent(mContext, InsightRecommendationDetailActivity::class.java)
//                i.putExtra("id", bookmarkList[position].bookmarked_item.id)
//                i.putExtra("isFromBookmark", true)
//                val intent = Intent(context, InsightRecommendationDetailActivity::class.java)
                i.putExtra("isFromBookmark", true)
                i.putExtra("id", bookmarkList[position].bookmarked_item.id)
                i.putExtra("insightImage", bookmarkList[position].bookmarked_item.image_path.toString())
                i.putExtra("title", bookmarkList[position].bookmarked_item.name)
                i.putExtra("time", bookmarkList[position].bookmarked_item.duration)
                i.putExtra("unit", bookmarkList[position].bookmarked_item.unit)
                (activity as BookmarksActivity).activityLauncher.launch(i,options, object : BaseActivityResult.OnActivityResult<ActivityResult> {
                    override fun onActivityResult(result: ActivityResult) {
                        if(result.resultCode == Activity.RESULT_OK){
                            if (result.data != null) {
                                val isRemoved = result.data!!.getBooleanExtra("removed",false)
                                val id = result.data!!.getIntExtra("id",-1)
                                if(isRemoved) {
                                    bookmarkList.forEachIndexed { index, element ->
                                        if (element.bookmarked_item.id == id) {
                                            bookmarkList.removeAt(index)
                                            binding.rvPosts.adapter?.notifyDataSetChanged()
                                            (activity as BookmarksActivity).updateHeader(bookmarkList.size,1)
                                            if(bookmarkList.isEmpty()){
                                                binding.noData.visibility = View.VISIBLE
                                            }
                                            else{
                                                binding.noData.visibility = View.GONE
                                            }
                                            return
                                        }
                                    }
                                }
                            }
                        }
                    }
                })
            }

        })
        binding.rvPosts.adapter = adapter
    }

    private fun getInsightBookmarks() {
        apiCall = retrofitClient.getWorkoutBookmark("insights")
        viewModel.getWorkoutBookmarks(apiCall)
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
                        ApiTags.GET_BOOKMARK -> {
                            val model = Gson().fromJson(it.data.toString(), BookMarksModel::class.java)
                            setBookMarks(model)
                        }
                    }
                }
            }
        })
    }

    private fun setBookMarks(model: BookMarksModel) {
        if (model != null && model.data != null && model.data.isNotEmpty()) {
            binding.noData.visibility = View.GONE
            bookmarkList.clear()
            bookmarkList.addAll(model.data)
            binding.rvPosts.adapter?.notifyDataSetChanged()
            (activity as BookmarksActivity).updateHeader(bookmarkList.size,1)
        }
        else{
            binding.noData.visibility = View.VISIBLE
            bookmarkList.clear()
            binding.rvPosts.adapter?.notifyDataSetChanged()
            (activity as BookmarksActivity).updateHeader(bookmarkList.size,1)
        }
    }

}