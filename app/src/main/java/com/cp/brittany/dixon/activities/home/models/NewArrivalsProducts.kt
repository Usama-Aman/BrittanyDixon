package com.cp.brittany.dixon.activities.home.models

data class NewArrivalsProductsModel(
    val `data`: NewArrivalsProductsData,
    val message: String,
    val status: String
)

data class NewArrivalsProductsData(
    val items: List<NewArrivalsProductsItem>,
    val total_items: Int
)

data class NewArrivalsProductsItem(
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

data class Categories(
    val description: String,
    val id: Int,
    val image: String,
    val image_path: String,
    val name: String
)

data class FirstImage(
    val file_name: String,
    val file_path: String,
    val id: Int,
    val product_id: Int
)

data class Image(
    val file_name: String,
    val file_path: String,
    val id: Int,
    val product_id: Int
)

data class MinimumPrice(
    val compare_at_price: String,
    val price: String
)

data class Size(
    val code: String,
    val colors: List<Color>,
    val created_at: String,
    val description: Any,
    val id: Int,
    val product_id: Int,
    val size: String,
    val updated_at: String,
    var isSelected : Boolean = false
    )

data class SubCategory(
    val description: String,
    val id: Int,
    val image: String,
    val image_path: String,
    val name: String
)

data class Color(
    val code: String,
    val color: String,
    val compare_at_price: String,
    val created_at: String,
    val description: Any,
    val id: Int,
    val price: String,
    val product_id: Int,
    val quantity: Int,
    val unit_cost: String,
    val currency: String,
    val size_id: Int,
    val updated_at: String,
    var isSelected : Boolean = false
)