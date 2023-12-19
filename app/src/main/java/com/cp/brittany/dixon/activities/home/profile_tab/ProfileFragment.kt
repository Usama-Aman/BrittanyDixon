package com.cp.brittany.dixon.activities.home.profile_tab

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.HomeActivity
import com.cp.brittany.dixon.activities.home.profile_setting.ProfileSettingActivity
import com.cp.brittany.dixon.activities.home.profile_tab.adapters.*
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.databinding.FragmentProfileBinding
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.*
import com.bumptech.glide.Glide
import com.cp.brittany.dixon.base.formatePickerDateNew2
import com.cp.brittany.dixon.base.longDateFormat
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.transition.MaterialFadeThrough
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
import com.michalsvec.singlerowcalendar.utils.DateUtils
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


open class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var mContext: Context

    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as HomeActivity).changeBarColor(R.color.light_blue_50)
        retrofitClient = RetrofitClient.getClient(requireContext()).create(Api::class.java)
        mContext = requireContext()

        initViews()
        initViewPager()
        initVM()
        initListeners()
    }

    private fun initViewPager() {
        binding.viewPager.isSaveEnabled = true
        binding.viewPager.offscreenPageLimit = 2
        val adapter = MyViewPagerAdapter(childFragmentManager, lifecycle)
        adapter.addFragment(TodayTaskFragment(), "TodayTaskFragment")
        adapter.addFragment(BookmarkFragment(), "BookmarkFragment")
        adapter.addFragment(ActivityFragment(), "ActivityFragment")
        binding.viewPager.adapter = adapter

        val tabLayoutMediator = TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            val simpleStoryView: View = getCustomTabAt(position)
            tab.customView = simpleStoryView
        }
        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val textView = tab?.customView?.findViewById<TextView>(R.id.tvTitle)
                textView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_blue_a200))
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val textView = tab?.customView?.findViewById<TextView>(R.id.tvTitle)
                textView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
        tabLayoutMediator.attach()
    }

    @SuppressLint("SetTextI18n")
    private fun getCustomTabAt(position: Int): View {
        val simpleView: View = LayoutInflater.from(requireContext()).inflate(R.layout.custom_tab, null)
        val textView: TextView = simpleView.findViewById(R.id.tvTitle)
        if (position == 0) {
            textView.text = "Today’s Task"
        } else if (position == 1) {
            textView.text = "Favourites"
        }
        else if (position == 2) {
            textView.text = "Activity"
        }
        return simpleView
    }

    private fun initViews() {
        val userData = AppUtils.getUserDetails(mContext)

        binding.tvProfileName.text = userData.name
        binding.tvEmail.text = userData.email

        Glide.with(mContext)
            .load(userData.image_path)
            .placeholder(R.drawable.ic_placeholder)
            .into(binding.ivProfile)
    }

    private fun initVM() {
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        binding.lifecycleOwner = this
    }

    private fun initListeners() {

        binding.ivSetting.setOnClickListener {
            AppUtils.preventDoubleClick(binding.ivSetting)
            startActivity(Intent(requireContext(), ProfileSettingActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        if (SharedPreference.getBoolean(requireContext(), Constants.isUserLoggedIn)) {
            val userData = AppUtils.getUserDetails(mContext)
            if (userData != null) {
                Glide.with(mContext)
                    .load(userData.image_path)
                    .placeholder(R.drawable.ic_placeholder)
                    .into(binding.ivProfile)
            }
        }
    }
}
