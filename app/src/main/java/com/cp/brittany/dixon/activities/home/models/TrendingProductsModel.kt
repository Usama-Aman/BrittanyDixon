package com.cp.brittany.dixon.activities.home.models

data class TrendingProductsModel(
    val `data`: TrendingProductsData,
    val message: String,
    val status: String
)

data class TrendingProductsData(
    val items: List<TrendingProductsItem>,
    val total_items: Int
)

data class TrendingProductsItem(
    val last_order: String,
    val product_data: TrendingProductData,
    val total_orders: Int
)

data class TrendingProductData(
    val categories: Categories,
    val first_image: FirstImage,
    val gender: String,
    val images: List<Image>,
    val minimum_price: MinimumPrice,
    val product_description: String,
    val product_id: Int,
    val product_name: String,
    val sizes: List<Size>,
    val sub_category: SubCategory
)