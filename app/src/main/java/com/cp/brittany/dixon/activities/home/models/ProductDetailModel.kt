package com.cp.brittany.dixon.activities.home.models

data class ProductDetailModel(
    val `data`: ProductDetailData,
    val message: String,
    val status: String
)

data class ProductDetailData(
    val avg_rating: Double,
    val categories: Categories,
    val category_id: Int,
    val category_name: CategoryName,
    val description: String,
    val files: ArrayList<File>,
    val first_image: String,
    val gender: String,
    val id: Int,
    val image: String,
    val minimum_price: MinimumPrice,
    val name: String,
    val sizes: List<Size>,
    val sold: String,
    val status: String,
    val stock: String,
    val sub_category: SubCategory,
    val sub_category_id: Int,
    val sub_category_name: SubCategoryName,
    var is_bookmarked: Int = 0
)
data class File(
    val file_name: String,
    val file_path: String,
    val id: Int,
    val product_id: Int
)

//data class ProductDetailModel(
//    val `data`: ProductDetailData,
//    val message: String,
//    val status: String
//)
//
//data class ProductDetailData(
//    val categories: Categories,
//    val category_id: Int,
//    val category_name: CategoryName,
//    val description: String,
//    val files: List<File>,
//    val first_image: FirstImage,
//    val gender: String,
//    val id: Int,
//    val image: String,
//    val minimum_price: MinimumPrice,
//    val name: String,
//    val sizes: List<Size>,
//    val status: String,
//    val sub_category: SubCategory,
//    val sub_category_id: Int,
//    val sub_category_name: SubCategoryName
//)
//


