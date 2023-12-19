package com.cp.brittany.dixon.activities.home.models

data class UserCardModel(
    val `data`: List<CardData>,
    val message: String,
    val status: String
)

data class DefaultCardModel(
    val `data`: CardData,
    val message: String,
    val status: String
)

data class CardData(
    val card_id: Int = 0,
    val card_type: String = "",
    val exp_month: Int = 0,
    val exp_year: Int = 0,
    var is_default: Int = 0,
    val last4: String = "",
    val user_id: Int = 0,
    var is_selected: Boolean = false
)