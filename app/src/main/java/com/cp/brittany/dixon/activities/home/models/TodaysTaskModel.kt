package com.cp.brittany.dixon.activities.home.models

data class TodayTasksModel(
    val `data`: TodayTasksData,
    val message: String,
    val status: String
)

data class TodayTasksData(
    val task: List<TodayTasks>,
    val total_tasks: Int
)

data class TodayTasks(
    val task: Task,
    val type: String,
    val url: String
)

data class Task(
    val cal_gain_reduce: String,
    val calories: String,
    val category_id: Int,
    val category_name: CategoryName,
    val created_at: String,
    val date: String,
    val description: String,
    val detail: String,
    val diet_prefrence_id: Int,
    val duration: String,
    val first_image: String,
    val food_category_id: Int,
    val food_status: Any,
    val gender: String,
    val id: Int,
    val image: String,
    val image_path: String,
    val insight_category_id: Int,
    val is_bookmarked: Int,
    val is_free: Int,
    val is_liked: Boolean,
    val level: Int,
    val minimum_price: MinimumPrice,
    val name: String,
    val recipe: String,
    val status: String,
    val sub_category_id: Int,
    val sub_category_name: SubCategoryName,
    val time: String,
    val total_duration: String,
    val total_likes: Int,
    val type: String,
    val unit: String,
    val updated_at: String,
    val weight_status: String,
    val workout_prefrence_id: Int,
    val video_path: String,
    val video: String,
    val percentage_completed: Double
)

data class CategoryName(
    val category_id: Int,
    val image: String,
    val image_path: String,
    val name: String
)

data class SubCategoryName(
    val description: String,
    val image: String,
    val image_path: String,
    val name: String,
    val sub_category_id: Int
)

