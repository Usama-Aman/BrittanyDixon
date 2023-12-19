package com.cp.brittany.dixon.activities.home.models


data class WorkoutSearchResponse(
    val `data`: List<WorkoutSearchFilterData>,
    val message: String,
    val status: String
)


data class WorkoutSearchFilterData(
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
    val percentage_completed: Double
)