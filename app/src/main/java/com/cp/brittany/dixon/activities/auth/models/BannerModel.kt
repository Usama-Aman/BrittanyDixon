package com.cp.brittany.dixon.activities.auth.models

data class BannerModel(
    val `data`: List<BannerData>,
    val message: String,
    val status: String
)

data class BannerData(
    val created_at: String,
    val description: String,
    val id: Int,
    val image: String,
    val image_path: String,
    val tags: Any,
    val title: String,
    val type: String,
    val updated_at: String
)