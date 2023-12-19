package com.cp.brittany.dixon.activities.home.models

data class SectionProductsModel(
    val `data`: List<SectionProductData>,
    val message: String,
    val status: String
)

data class SectionProductData(
    val description: String,
    val id: Int,
    val image: String,
    val image_path: String,
    val name: String,
    val section_products: List<SectionProduct>
)

data class SectionProduct(
    val avg_rating: Double,
    val category_id: Int,
    val category_name: CategoryName,
    val description: String,
    val first_image: String,
    val gender: String,
    val id: Int,
    val image: String,
    val is_bookmarked: Int,
    val minimum_price: MinimumPrice,
    val name: String,
    val pivot: Pivot,
    val sold: Int,
    val status: String,
    val stock: String,
    val sub_category_id: Int,
    val sub_category_name: CategoryName
)


data class Pivot(
    val product_id: Int,
    val product_section_id: Int
)