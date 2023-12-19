package com.cp.brittany.dixon.activities.home.home_models

data class AllWorkoutModel(
    val `data`: Data,
    val message: String,
    val status: String
)

data class Data(
    val categories: List<AllWorkoutCategory>,
    val images_baseUrl: ImagesBaseUrl
)

data class AllWorkoutCategory(
    val category_id: Int,
    val category_name: String,
    val description: String,
    val image: String,
    val image_path: String,
    val no_equipment: Int,
    val workouts: List<WorkoutList>,
    var isPremium: Boolean = false
)

data class ImagesBaseUrl(
    val workouts: String
)

data class WorkoutList(
    val cal_gain_reduce: String,
    val category_id: Int,
    val description: String,
    val duration: String,
    val gender: String,
    val id: Int,
    val image: String,
    val image_path: String,
    val is_bookmarked: Int,
    val is_free: Int,
    val level: Int,
    val name: String,
    val total_duration: String,
    val unit: String,
    val weight_status: String,
    val workout_prefrence_id: Int,
    val percentage_completed: Double,
    val total_views: Int
)