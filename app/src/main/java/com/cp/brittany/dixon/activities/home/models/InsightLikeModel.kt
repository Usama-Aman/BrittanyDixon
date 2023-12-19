package com.cp.brittany.dixon.activities.home.models

data class InsightLikeModel(
    val `data`: InsightLikeData?,
    val message: String,
    val status: String
)
data class InsightBookMarkModel(
    val `data`: List<InsightLikeData>,
    val message: String,
    val status: String
)

data class InsightLikeData(
    val created_at: String,
    val id: Int,
    val insight_id: String,
    val type: String,
    val updated_at: String,
    val user_id: Int
)