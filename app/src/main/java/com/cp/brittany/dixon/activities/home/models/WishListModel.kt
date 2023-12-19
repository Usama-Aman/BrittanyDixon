package com.cp.brittany.dixon.activities.home.models

data class WishListModel(
    val `data`: List<WishListData>,
    val message: String,
    val status: String
)

data class WishListData(
    val bookmarked_item: WishListItem,
    val created_at: String,
    val id: Int,
    val table: String,
    val table_id: String,
    val updated_at: String,
    val user_id: Int
)

data class WishListItem(
    val avg_rating: Any,
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
    val sold: Any,
    val status: String,
    val stock: String,
    val sub_category_id: Int,
    val sub_category_name: SubCategoryName
)