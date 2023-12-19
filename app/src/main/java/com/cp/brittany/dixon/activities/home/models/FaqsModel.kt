package com.cp.brittany.dixon.activities.home.models

data class FaqsModel(
    val `data`: List<FaqsData>,
    val message: String,
    val status: String
)

data class FaqsData(
    val answer: String,
    val created_at: Any,
    val id: Int,
    val question: String,
    val updated_at: Any
)