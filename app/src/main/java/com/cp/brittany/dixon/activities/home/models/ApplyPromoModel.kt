package com.cp.brittany.dixon.activities.home.models

data class ApplyPromoModel(
    val `data`: ApplyPromoData,
    val message: String,
    val status: String
)

data class ApplyPromoData(
    val amount: String,
    val code: String,
    val currency: String,
    val id: Int,
    val order_minimum_amount: String
)