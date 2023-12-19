package com.cp.brittany.dixon.activities.home.models

data class Subscription(val id: Int, val timePeriod: String, val details: String, val price: Double, val unit: String, var isSelected: Boolean = false)