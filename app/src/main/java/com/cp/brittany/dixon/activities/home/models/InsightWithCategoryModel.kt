package com.cp.brittany.dixon.activities.home.models

data class InsightWithCategoryModel(
    val `data`: List<InsightWithCategoryData>,
    val message: String,
    val status: String
)

data class InsightWithCategoryData(
    val created_at: String = "",
    val date: String = "",
    val detail: String = "",
    val duration: String = "",
    val id: Int = 0,
    val image: String = "",
    val image_path: String = "",
    val insight_category_id: Int = 0,
    var is_bookmarked: Int = 0,
    val is_liked: Boolean = false,
    val name: String = "",
    val total_likes: Int = 0,
    val unit: String = "",
    val updated_at: String = ""
)