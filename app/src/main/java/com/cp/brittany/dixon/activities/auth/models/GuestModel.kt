package com.cp.brittany.dixon.activities.auth.models

data class GuestModel(
    val `data`: UserData,
    val message: String,
    val status: String
)

//data class GuestData(
//    val _token: String,
//    val user: User
//)
//
//data class User(
//    val address: Any,
//    val city: Any,
//    val country: Any,
//    val created_at: String,
//    val date_of_birth: Any,
//    val device_id: String,
//    val diet_prefrence_id: Any,
//    val email: String,
//    val email_verified_at: Any,
//    val expiration_date: Any,
//    val gender: Any,
//    val height: Any,
//    val id: Int,
//    val image: Any,
//    val image_path: String,
//    val is_blocked: Int,
//    val is_email_verified: Int,
//    val is_guest: Int,
//    val is_premium_user: Int,
//    val name: String,
//    val payment_provider: Any,
//    val payment_token: Any,
//    val plan_id: Any,
//    val provider: Any,
//    val provider_id: Any,
//    val social_id: Any,
//    val social_type: Any,
//    val state: Any,
//    val stripe_user_id: Any,
//    val updated_at: String,
//    val verification_code: Any,
//    val weight: Any,
//    val workout_prefrence_id: Any,
//    val zip_code: Any
//)