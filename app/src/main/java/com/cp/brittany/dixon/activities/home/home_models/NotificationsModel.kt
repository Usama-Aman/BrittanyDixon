package com.cp.brittany.dixon.activities.home.home_models
data class NotificationsModel(
    val `data`: List<NotificationsList>,
    val message: String,
    val status: String
)

data class NotificationsList(
    val created_at: Any,
    val from_id: Any,
    val from_type: Any,
    val id: Int,
    val image: Any,
    var is_read: Int,
    val notification: String,
    val notification_data: NotificationData,
    val notification_type: String,
    val title: String,
    val type_id: String,
    val updated_at: Any
)

data class NotificationData(
    val body_part: String,
    val cal_gain_reduce: String,
    val category_id: Int,
    val created_at: String,
    val date: String,
    val description: String,
    val detail: String,
    val duration: String,
    val gender: String,
    val id: Int,
    val image: String,
    val image_path: String,
    val insight_category_id: Int,
    val is_bookmarked: Int,
    val is_free: Int,
    val is_liked: Boolean,
    val level: Int,
    val name: String,
    val total_duration: String,
    val total_likes: Int,
    val unit: String,
    val updated_at: String,
    val weight_status: String,
    val workout_prefrence_id: Int
)
/*data class NotificationsModel(
    val `data`: List<NotificationsList>,
    val message: String,
    val status: String
)

data class NotificationsList(
    val created_at: Any,
    val from_id: Any,
    val from_type: Any,
    val id: Int,
    val image: Any,
    val notification: String,
    val notification_data: NotificationData,
    val notification_type: String,
    val title: String,
    val type_id: String,
    val updated_at: Any,
    var is_read: Int
)

data class NotificationData(
    val name: String,
    val id: Int,
    val image_path: String
)*/