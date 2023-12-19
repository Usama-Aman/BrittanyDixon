package com.cp.brittany.dixon.activities.home.home_models

data class FoodDetailsModel(
    val `data`: FoodDetailsData,
    val message: String,
    val status: String
)

data class FoodDetailsData(
    val foods: FoodsDetails,
    val images_baseUrl: AllFoodsImagesBaseUrl
)

data class FoodsDetails(
    val calories: String,
    val created_at: Any,
    val diet_prefrence_id: Int,
    val food_categories: FoodCategories,
    val food_category_id: Int,
    val food_status: Any,
    val id: Int,
    val image: String,
    val image_path: String,
    val video_path: String,
    val video: String,
    val thumbnail_path: String,
    val ingredients: List<Ingredients>,
    val name: String,
    val recipe: String,
    val time: String,
    val type: String,
    val unit: String,
    val updated_at: String,
    val weight_status: String,
    val food_preferences :FoodPreferences,
    var is_bookmarked: Int
)

data class FoodCategories(
    val created_at: Any,
    val description: String,
    val id: Int,
    val name: String,
    val updated_at: Any
)

data class Ingredients(
    val name: String,
    val pivot: Pivot
)

data class Pivot(
    val food_id: Int,
    val ingredient_id: Int,
    val quantity: String,
    val unit: String
)

data class FoodPreferences(
    val created_at: String,
    val description: String,
    val id: Int,
    val image: String,
    val image_path: String,
    val is_selected: Int,
    val name: String,
    val updated_at: String
)