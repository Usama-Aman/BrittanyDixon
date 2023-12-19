package com.cp.brittany.dixon.activities.auth.models

data class LoginModel(
    val `data`: UserData,
    val message: String,
    val status: String
)

