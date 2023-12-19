package com.cp.brittany.dixon.activities.home.models

data class ChangeFoodStatusModel(
    val `data`: ChangeFoodStatusData,
    val message: String,
    val status: String
)

data class ChangeFoodStatusData(
    val created_at: String,
    val date: String,
    val food_id: String,
    val id: Int,
    val status: String,
    val updated_at: String,
    val user_id: Int
)