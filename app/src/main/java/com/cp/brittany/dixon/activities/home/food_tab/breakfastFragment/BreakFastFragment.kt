package com.cp.brittany.dixon.activities.home.food_tab.breakfastFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cp.brittany.dixon.databinding.FragmentBreakFastBinding

class BreakFastFragment : Fragment() {
    private lateinit var binding: FragmentBreakFastBinding
    private lateinit var featuredFoodAdapter: FeaturedFoodAdapter
    private lateinit var findRecipesAdapter: FindRecipesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBreakFastBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listener()
        setFeaturedFoodRecView()
    }


    private fun listener() {

    }

    private fun setFeaturedFoodRecView() {
        featuredFoodAdapter = FeaturedFoodAdapter(requireContext())
        binding.rvFeaturedMeal.adapter = featuredFoodAdapter
    }

}