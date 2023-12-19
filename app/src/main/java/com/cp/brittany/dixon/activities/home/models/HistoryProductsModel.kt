package com.cp.brittany.dixon.activities.home.models
data class HistoryProductsModel(
    val `data`: HistoryProductsData,
    val message: String,
    val status: String
)

data class HistoryProductsData(
    val items: List<HistoryProductsItem>,
    val total_items: Int
)

data class HistoryProductsItem(
    val history_id: Int,
    val occurences: String,
    val product_data: HistoryProductsProductData,
    val product_id: Int,
    val type: String
)

data class HistoryProductsProductData(
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