package com.cp.brittany.dixon.activities.home.models

data class InsightsRecommendationModel(
    val `data`: List<InsightsRecommendationData>,
    val message: String,
    val status: String
)

data class InsightsRecommendationData(
    val created_at: String,
    val date: String,
    val detail: String,
    val duration: String,
    val id: Int,
    val image: String,
    val image_path: String,
    val insight_category_id: Int,
    var is_bookmarked: Int,
    val is_liked: Boolean,
    val name: String,
    val total_likes: Int,
    val unit: String,
    val updated_at: String
)