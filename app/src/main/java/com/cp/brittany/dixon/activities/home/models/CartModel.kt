package com.cp.brittany.dixon.activities.home.models

data class CartModel(
    val `data`: CartData,
    val message: String,
    val status: String
)

data class CartData(
    val app_charges: String = "",
    val currency: String = "",
    val items: List<CartItem> = ArrayList(),
    val shipping_days: String = "",
    val shipping_fee: String = "",
    val tax: String = "",
    val total_items: Int = 0,
    val total_price: Int = 0
)

data class CartItem(
    val cart_id: Int,
    val category_name: String,
    val color: CartColor,
    val description: String,
    val gender: String,
    val images: List<Image>,
    val price: CartPrice,
    val product_id: Int,
    val product_name: String,
    var quantity: String,
    val size: CartSize,
    val sub_category_name: String
)

data class CartColor(
    val color: String,
    val color_code: String,
    val color_id: Int
)

data class CartPrice(
    val compare_at_price: String,
    val price: String
)

data class CartSize(
    val code: String,
    val size: String,
    val size_id: Int
)
