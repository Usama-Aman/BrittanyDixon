package com.cp.brittany.dixon.activities.home.models

data class FeedbackModel(
    val `data`: FeedbackData,
    val message: String,
    val status: String
)

data class FeedbackData(
    val created_at: String,
    val feedback: String,
    val id: Int,
    val rating: String,
    val updated_at: String,
    val user_id: Int
)