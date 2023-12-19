package com.cp.brittany.dixon.activities.home.shops_tab

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cp.brittany.dixon.databinding.FragmentComingShopBinding


class ComingShopFragment : Fragment() {
    private lateinit var binding: FragmentComingShopBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentComingShopBinding.inflate(layoutInflater,container,false)
        return binding.root
    }
}