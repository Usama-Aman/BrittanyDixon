package com.cp.brittany.dixon.activities.home.models

data class SearchFoodResponse(
    val `data`: List<SearchFoodModel>,
    val message: String,
    val status: String
)

data class SearchFoodModel(
    val calories: String,
    val created_at: Any,
    val diet_prefrence_id: Int,
    val food_category_id: Int,
    val food_status: Any,
    val id: Int,
    val image: String,
    val image_path: String,
    val name: String,
    val recipe: String,
    val time: String,
    val type: String,
    val unit: String,
    val updated_at: String,
    val weight_status: String,
    var is_bookmarked: Int,
    val video_path: String,
    val video: String
)