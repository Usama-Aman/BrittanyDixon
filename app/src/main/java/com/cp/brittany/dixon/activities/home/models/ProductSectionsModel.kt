package com.cp.brittany.dixon.activities.home.models

data class ProductSectionsModel(
    val `data`: List<ProductSectionsData>,
    val message: String,
    val status: String
)

data class ProductSectionsData(
    val description: String,
    val id: Int,
    val image: String,
    val image_path: String,
    val name: String
)