package com.cp.brittany.dixon.activities.home.models

data class ProductCategoriesModel(
    val `data`: List<ProductCategoriesData>,
    val message: String,
    val status: String
)

data class ProductCategoriesData(
    val created_at: String = "",
    val description: String = "",
    val id: Int = 0,
    val image: String = "",
    val image_path: String = "",
    val name: String = "",
    val updated_at: String = ""
)