package com.cp.brittany.dixon.activities.home.podcast

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.podcast.PlayPodcastActivity.Companion.podcastsList
import com.cp.brittany.dixon.databinding.BottomSheetPodcastListBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PodcastListBottomSheet(val podcastInterface: PodcastsAdapter.PodcastInterface) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetPodcastListBinding
    private lateinit var podcastsAdapter: PodcastsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.BottomSheetDialogTheme)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.BottomDialogAnimation
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.layout_bottom_sheet, container, false)
        binding = BottomSheetPodcastListBinding.inflate(layoutInflater, container, false)

        initViews()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val bottomSheetDialog = dialog
        if (bottomSheetDialog != null) {

            val lp = WindowManager.LayoutParams()
            lp.copyFrom(dialog?.window?.attributes)
            val displayMetrics = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)

            lp.width = (displayMetrics.widthPixels)
            bottomSheetDialog.window?.attributes = lp
        }
    }

    private fun initViews() {
        podcastsAdapter = PodcastsAdapter(podcastsList, object : PodcastsAdapter.PodcastInterface {
            override fun onPodcastClicked(position: Int) {
                podcastInterface.onPodcastClicked(position)
                dismiss()
            }
        })
        binding.podcastRecyclerView.adapter = podcastsAdapter

    }


}