package com.cp.brittany.dixon.activities.home.models

data class BookmarkModel(
    val `data`: BookmarkModelData,
    val message: String,
    val status: String
)

data class BookmarkModelData(
    val foods: ArrayList<Food>,
    val insights: ArrayList<Insight>,
    val products: ArrayList<Product>,
    val workouts: ArrayList<Workout>
)

data class Insight(
    val bookmarked_item: InsightBookmarkedItem,
    val created_at: String,
    val id: Int,
    val table: String,
    val table_id: String,
    val updated_at: String,
    val user_id: Int
)

data class Product(
    val bookmarked_item: ProductBookmarkedItem,
    val created_at: String,
    val id: Int,
    val table: String,
    val table_id: String,
    val updated_at: String,
    val user_id: Int
)

data class Workout(
    val bookmarked_item: WorkoutBookmarkedItem,
    val created_at: String,
    val id: Int,
    val table: String,
    val table_id: String,
    val updated_at: String,
    val user_id: Int
)

data class InsightBookmarkedItem(
    val created_at: String,
    val date: String,
    val detail: String,
    val duration: String,
    val id: Int,
    val image: String,
    val image_path: String,
    val insight_category_id: Int,
    val is_bookmarked: Int,
    val is_liked: Boolean,
    val name: String,
    val podcast: Any,
    val podcast_path: String,
    val podcast_thumbnail: Any,
    val podcast_thumbnail_path: String,
    val total_likes: Int,
    val unit: String,
    val updated_at: String
)

data class ProductBookmarkedItem(
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

data class WorkoutBookmarkedItem(
    val body_part: String,
    val cal_gain_reduce: String,
    val category_id: Int,
    val description: String,
    val duration: String,
    val gender: String,
    val id: Int,
    val image: Any,
    val image_path: String,
    val is_bookmarked: Int,
    val is_free: Int,
    val level: Int,
    val name: String,
    val percentage_completed: Double,
    val status: String,
    val total_duration: String,
    val unit: String,
    val weight_status: String,
    val workout_prefrence_id: Int
)



data class Food(
    val bookmarked_item: FoodBookmarkedItem,
    val created_at: String,
    val id: Int,
    val table: String,
    val table_id: String,
    val updated_at: String,
    val user_id: Int
)

data class FoodBookmarkedItem(
    val calories: String,
    val created_at: String,
    val diet_prefrence_id: Int,
    val food_category_id: Int,
    val food_status: Any,
    val id: Int,
    val image: String,
    val image_path: String,
    val is_bookmarked: Int,
    val name: String,
    val podcast: String,
    val recipe: String,
    val thumbnail: String,
    val thumbnail_path: String,
    val time: String,
    val type: String,
    val unit: String,
    val updated_at: String,
    val video: String,
    val video_path: String,
    val weight_status: String
)
