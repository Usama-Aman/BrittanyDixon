package com.cp.brittany.dixon.activities.home.models

data class InsightCategoriesModel(
    val `data`: List<InsightCategoriesData>,
    val message: String,
    val status: String
)

data class InsightCategoriesData(
    val created_at: String = "",
    val description: String = "",
    val diet_prefrence_id: Int = 0,
    val gender: String = "",
    val id: Int = 0,
    val name: String = "",
    val updated_at: String = "",
    val workout_prefrence_id: Int = 0
)