package com.cp.brittany.dixon.activities.auth.models

data class RegisterModel(
    val `data`: UserData,
    val message: String,
    val status: String
)

data class UserData(
    val _token: String,
    val user: UserDetailData,
    val verification_code: Int
)

data class UserDetailData(
    val address: String,
    val city: Any,
    val country: Any,
    val created_at: String,
    val date_of_birth: Any,
    val email: String,
    val email_verified_at: Any,
    val gender: Any,
    val height: Height,
    val id: Int,
    var image: String,
    var is_email_verified: Int,
    val is_guest: Int,
    val name: String,
    val provider: Any,
    val provider_id: Any,
    val social_id: Any,
    val social_type: Any,
    val state: Any,
    val stripe_user_id: Any,
    val updated_at: String,
    val verification_code: String,
    val weight: Weight,
    var workout_prefrence_id: String,
    var diet_prefrence_id: String,
    val zip_code: Any,
    val device_id: String,
    var image_path: String,
    var isSubscribed: Boolean = false,
    var total_workout_preferences: Int = 0,
    var total_diet_preferences: Int = 0
)

data class Height(
    val height: Any,
    val height_unit: String
)

data class Weight(
    val weight: Any,
    val weight_unit: String
)