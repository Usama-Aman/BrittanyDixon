package com.cp.brittany.dixon.activities.home.faq_feedback

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.faq_feedback.faqs_tab.FaqsFragment
import com.cp.brittany.dixon.activities.home.faq_feedback.feedback_tab.FeedbackFragment
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivityFaqFeedbackBinding

class FaqFeedbackActivity : BaseActivity() {
    private lateinit var binding: ActivityFaqFeedbackBinding
    private lateinit var vpf: ViewPagerFragmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaqFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()

        initViews()
        initListeners()
        setViewPager()
    }

    private fun initViews() {

    }

    private fun initListeners() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.tvFaqs.setOnClickListener {
            binding.viewPager.currentItem = 0
        }
        binding.tvFeedback.setOnClickListener {
            binding.viewPager.currentItem = 1
        }
    }

    private fun setViewPager() {
        vpf = ViewPagerFragmentAdapter(this@FaqFeedbackActivity)
        binding.viewPager.adapter = vpf
        binding.viewPager.registerOnPageChangeCallback(pageChangeCallback)
        binding.viewPager.isUserInputEnabled = false
    }

    private var pageChangeCallback: ViewPager2.OnPageChangeCallback =
        object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                colorControl(position)
            }
        }

    private fun colorControl(i: Int) {

        binding.tvFaqs.setTextColor(resources.getColor(if (i == 0) R.color.blue_a700 else R.color.home_heading_text))
        binding.tvFeedback.setTextColor(resources.getColor(if (i == 1) R.color.blue_a700 else R.color.home_heading_text))

    }

    private class ViewPagerFragmentAdapter(
        fragmentActivity: FragmentActivity
    ) :
        FragmentStateAdapter(fragmentActivity) {
        private val context: Context = fragmentActivity

        override fun createFragment(position: Int): Fragment {


            when (position) {
                0 -> return FaqsFragment()
                1 -> return FeedbackFragment()
            }
            return FaqsFragment()
        }

        override fun getItemCount(): Int {
            return 2
        }
    }
}