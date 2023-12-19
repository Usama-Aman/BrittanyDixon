package com.cp.brittany.dixon.activities.home.my_orders

data class MyOrdersModel(
    val `data`: List<MyOrdersData>,
    val message: String,
    val status: String
)

data class MyOrdersData(
    val app_charges: String,
    val card_data: String,
    val discount: String,
    val grand_total: String,
    val id: Int,
    val order_detail: List<OrderDetail>,
    val order_number: String,
    val promo_code_data: Any,
    val promo_code_id: Any,
    val shipping_address_data: String,
    val shipping_address_id: Int,
    val shipping_charges: String,
    val status: String,
    val sub_total: String,
    val tax: String,
    val transection_id: String,
    val user_id: Int
)

data class OrderDetail(
    val color: String,
    val color_data: String,
    val color_id: Int,
    val discount: String,
    val grand_total: String,
    val id: Int,
    val order_id: Int,
    val price: String,
    val product: Product,
    val product_data: String,
    val product_id: Int,
    val product_name: String,
    val quantity: Int,
    val size: String,
    val size_data: String,
    val size_id: Int,
    val sub_total: String,
    val tax: String,
    val user_data: String,
    val user_id: Int
)

data class Product(
    val category_id: Int,
    val category_name: CategoryName,
    val description: String,
    val files: List<File>,
    val first_image: String,
    val gender: String,
    val id: Int,
    val image: String,
    val minimum_price: MinimumPrice,
    val name: String,
    val status: String,
    val sub_category: SubCategory,
    val sub_category_id: Int,
    val sub_category_name: SubCategoryName
)

data class CategoryName(
    val category_id: Int,
    val image: String,
    val image_path: String,
    val name: String
)

data class File(
    val file_name: String,
    val file_path: String,
    val id: Int,
    val product_id: Int
)

data class FirstImage(
    val file_name: String,
    val file_path: String
)

data class MinimumPrice(
    val compare_at_price: String,
    val price: String
)

data class SubCategory(
    val category_id: Int,
    val description: String,
    val id: Int,
    val image: String,
    val image_path: String,
    val name: String
)

data class SubCategoryName(
    val description: String,
    val image: String,
    val image_path: String,
    val name: String,
    val sub_category_id: Int
)