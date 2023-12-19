package com.cp.brittany.dixon.activities.home.profile_tab.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.models.TodayTasks
import com.cp.brittany.dixon.databinding.ItemProfileTabImagesBinding
import com.cp.brittany.dixon.databinding.ItemProfileTabMainBinding
import com.bumptech.glide.Glide
import com.cp.brittany.dixon.utils.AppUtils
import com.cp.brittany.dixon.utils.viewGone
import com.cp.brittany.dixon.utils.viewVisible

class ProfileTabTasksAdapter(
    var todayTasks: MutableList<TodayTasks>,
    val profileAdapterInterface: ProfileAdapterInterface
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var mContext: Context

    companion object {
        const val ITEM_TYPE = 0
        const val ITEM_TYPE_IMAGE = 1
    }

    interface ProfileAdapterInterface {
        fun onItemClicked(position: Int, option: ActivityOptionsCompat)
        fun onImageClicked(position: Int, option: ActivityOptionsCompat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mContext = parent.context
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == ITEM_TYPE) {
            val binding: ItemProfileTabMainBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.item_profile_tab_main,
                parent,
                false
            )
            return TasksViewHolder(binding)
        } else {
            val binding: ItemProfileTabImagesBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.item_profile_tab_images,
                parent,
                false
            )
            return PictureHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TasksViewHolder -> holder.onBind(position)
            is PictureHolder -> holder.onBind(position)
        }
    }

    override fun getItemCount(): Int = todayTasks.size

    // workouts,foods,insights,products
    override fun getItemViewType(position: Int): Int {
        return if (todayTasks[position].type == "image")
            ITEM_TYPE_IMAGE
        else ITEM_TYPE
    }


    inner class TasksViewHolder(
        var binding: ItemProfileTabMainBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(position: Int) {
            if (position == todayTasks.size - 1) {
                binding.view.viewGone()
            }
            if (todayTasks[position].task != null) {
                when (todayTasks[position].type) {
                    "foods" -> {
                        binding.ivCategoryImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_food_active))
                        binding.llTimeAndCalories.viewVisible()
                        binding.llTime.viewGone()
                        binding.llLevelsAndWeeks.viewGone()
                        binding.llProduct.viewGone()

                        binding.tvTime.text = "$${todayTasks[position].task.time} ${todayTasks[position].task.unit}"
                        if (todayTasks[position].task.calories == "1")
                            binding.tvCalories.text = "${todayTasks[position].task.calories} Calorie"
                        else
                            binding.tvCalories.text = "${todayTasks[position].task.calories} Calories"

                        Glide.with(mContext)
                            .load(todayTasks[position].task.image_path)
                            .into(binding.ivProfileImage)
                    }
                    "workouts" -> {
                        binding.ivCategoryImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_workout_active))
                        binding.llLevelsAndWeeks.viewVisible()
                        binding.llTime.viewGone()
                        binding.llTimeAndCalories.viewGone()
                        binding.llProduct.viewGone()

                        binding.tvLevel.text = "Level ${todayTasks[position].task.level.toString()}"
                        binding.tvWeeks.text = "${todayTasks[position].task.duration} ${todayTasks[position].task.unit}"

                        Glide.with(mContext)
                            .load(todayTasks[position].task.image_path)
                            .into(binding.ivProfileImage)
                    }
                    "insights" -> {
                        binding.ivCategoryImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_insight_active))
                        binding.llTime.viewVisible()
                        binding.llTimeAndCalories.viewGone()
                        binding.llLevelsAndWeeks.viewGone()
                        binding.llProduct.viewGone()

                        if (todayTasks[position].task.duration != null)
                            binding.tvInsightTime.text = "${todayTasks[position].task.duration} ${todayTasks[position].task.unit}"

                        Glide.with(mContext)
                            .load(todayTasks[position].task.image_path)
                            .into(binding.ivProfileImage)
                    }
                    "products" -> {
                        binding.ivCategoryImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_shop_active))
                        binding.llProduct.viewVisible()
                        binding.llTime.viewGone()
                        binding.llTimeAndCalories.viewGone()
                        binding.llLevelsAndWeeks.viewGone()

                        if (todayTasks[position].task.minimum_price != null)
                            binding.tvInsightTime.text = "$${todayTasks[position].task.minimum_price.price}"

                        Glide.with(mContext)
                            .load(todayTasks[position].task.first_image)
                            .into(binding.ivProfileImage)
                    }
                }

                binding.tvName.text = todayTasks[position].task.name

                itemView.setOnClickListener {
                    AppUtils.preventDoubleClick(itemView)
                    when (todayTasks[position].type) {
                        "workouts" -> {
                            val p1 = Pair.create<View, String>((binding.ivProfileImage as View?)!!, "transitionImage")
                            val p2 = Pair.create<View, String>((binding.tvName as View?)!!, "title")
                            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                (mContext as AppCompatActivity),
                                p1, p2
                            )
                            profileAdapterInterface.onItemClicked(position, options)
                        }
                        "insights" -> {
                            val p1 = Pair.create<View, String>((binding.ivProfileImage as View?)!!, "transitionImage")
                            val p2 = Pair.create<View, String>((binding.tvName as View?)!!, "title")
                            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                (mContext as AppCompatActivity),
                                p1, p2
                            )
                            profileAdapterInterface.onItemClicked(position, options)
                        }
                        "products" -> {
                            val p1 = Pair.create<View, String>((binding.ivProfileImage as View?)!!, "transitionImage")
                            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                (mContext as AppCompatActivity),
                                p1
                            )
                            profileAdapterInterface.onItemClicked(position, options!!)
                        }
                        "foods" -> {
                            val p1 = Pair.create<View, String>((binding.ivProfileImage as View?)!!, "transitionImage")
                            val p2 = Pair.create<View, String>((binding.tvName as View?)!!, "title")

                            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                (mContext as AppCompatActivity),
                                p1, p2
                            )
                            profileAdapterInterface.onItemClicked(position, options!!)
                        }
                    }


                }

            }
        }
    }

    inner class PictureHolder(
        var binding: ItemProfileTabImagesBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(position: Int) {

            Glide.with(mContext)
                .load(todayTasks[position].task.image_path)
                .into(binding.ivPicture)
            if (position == todayTasks.size - 1) {
                binding.view.viewGone()
            }
            binding.ivPicture.setOnClickListener {
                AppUtils.preventDoubleClick(binding.ivPicture)
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (mContext as AppCompatActivity),
                    (binding.ivPicture as View?)!!, "image"
                )
                profileAdapterInterface.onImageClicked(position, options)
            }
        }
    }
}