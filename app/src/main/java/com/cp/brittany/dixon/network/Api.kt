package com.cp.brittany.dixon.network

import com.cp.brittany.dixon.activities.home.search.food_tab.FoodFilterResultIngredient
import com.cp.brittany.dixon.base.AppController
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface Api {

    @GET("api/getCountryCityState")
    fun getCountries(
    ): Call<ResponseBody>


    @GET("api/getCountryCityState")
    fun getStates(
        @Query("type") type: String,
        @Query("country_id") country_id: Int,
    ): Call<ResponseBody>

    @GET("api/getCountryCityState")
    fun getCities(
        @Query("type") type: String,
        @Query("country_id") country_id: Int,
        @Query("state_id") state_id: Int,
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("api/socialLogin")
    fun socialLogin(
        @Field("social_id") social_id: String,
        @Field("social_type") social_type: String,
        @Field("email") email: String,
        @Field("name") name: String,
        @Field("device_id") device_id: String,
        @Field("fcm_token") fcm_token: String,
        @Field("device_type") device_type: String = "android",
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("password_confirmation") password_confirmation: String,
        @Field("device_id") device_id: String,
        @Field("fcm_token") fcm_token: String,
        @Field("device_type") device_type: String = "android",
    ): Call<ResponseBody>
    @FormUrlEncoded
    @POST("api/createGuestUser")
    fun registerGuest(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("password_confirmation") password_confirmation: String,

    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("device_id") device_id: String,
        @Field("fcm_token") fcm_token: String,
        @Field("device_type") device_type: String = "android",
    ): Call<ResponseBody>

    @POST("api/logout")
    fun logOut(
    ): Call<ResponseBody>

    @GET("api/createGuestUser")
    fun loginAsGuest(
        @Query("device_id") deviceId: String,
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/getBanner")
    fun getBanner(
        @Field("type") type: String,
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/forgotPasswordRequest")
    fun forgotPassword(
        @Field("email") email: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/resendCode")
    fun resendOtp(
        @Field("email") email: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/verifyEmail")
    fun verifyOTP(
        @Field("code") code: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/forgotPassword")
    fun resetPassword(
        @Field("email") email: String,
        @Field("code") code: String,
        @Field("password") password: String,
        @Field("password_confirmation") password_confirmation: String,
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/assignPreferenceToUser")
    fun assignWorkoutPreference(
        @Field("workout_prefrence_ids") workout_prefrence_id: String,
        @Field("type") type: String,
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/assignPreferenceToUser")
    fun assignDietPreference(
        @Field("diet_prefrence_ids") diet_prefrence_id: String,
        @Field("type") type: String,
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/allFoods")
    fun getAllFoods(
        @Field("skip") skip: Int = 0,
        @Field("take") take: Int = AppController.PAGINATION_COUNT
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/getFoodsByType")
    fun getFeaturedFoodByType(
        @Field("type") type: String
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("api/addFoodToUserFoods")
    fun addRemoveFoodToUserFood(
        @Field("type") type: String,
        @Field("food_id") food_id: Int,
        @Field("action") action: String
    ): Call<ResponseBody>

    @GET("api/getFoodsByType")
    fun getFoodByType(
        @Query("type") type: String
    ): Call<ResponseBody>

    @POST("api/searchFoodByName")
    fun foodSimpleSearch(
        @Query("search") search: String,
        @Query("skip") skip: Int = 0,
        @Query("take") take: Int = AppController.PAGINATION_COUNT
    ): Call<ResponseBody>

    @GET("api/getUserNotification")
    fun getNotifications(): Call<ResponseBody>

    @GET("api/getWorkoutPrefrences")
    fun getWorkoutPreferences(): Call<ResponseBody>

    @GET("api/getDietPrefrences")
    fun getDietaryPreferences(): Call<ResponseBody>

    @GET("api/getallWorkouts")
    fun getAllWorkouts(): Call<ResponseBody>

    @GET("api/workoutDetail/{id}")
    fun getWorkoutDetails(
        @Path("id") id: Int
    ): Call<ResponseBody>

    @GET("api/getWorkoutBySection/{id}")
    fun getWorkoutLessons(
        @Path("id") id: Int
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/reset_workout_progress")
    fun resetWorkout(
        @Field("workout_id") food_id: Int,
    ): Call<ResponseBody>

    @GET("api/getFoodById/{id}")
    fun getFoodDetails(
        @Path("id") id: Int
    ): Call<ResponseBody>

    @GET("api/getProductCategories")
    fun getProductCategories(
        @Query("skip") skip: Int,
        @Query("take") take: Int = AppController.PAGINATION_COUNT
    ): Call<ResponseBody>

    @GET("api/getNewArrivalsByCategory/{id}")
    fun getNewArrivals(@Path("id") id: Int = 0): Call<ResponseBody>

    @GET("api/getProductsBySection/{id}")
    fun getSectionProductsById(@Path("id") id: Int = 0): Call<ResponseBody>

    @GET("api/trendingProductsByCategory/{id}")
    fun getTrendingProduct(@Path("id") id: Int = 0): Call<ResponseBody>

    @GET("api/trendingProductsByCategory")
    fun getAllTrendingProduct(): Call<ResponseBody>

    @GET("api/getUserProductHistoryByCategory/{id}")
    fun getHistoryProducts(
        @Path("id") id: Int = 0,
        @Query("skip") skip: Int,
        @Query("take") take: Int = AppController.PAGINATION_COUNT
    ): Call<ResponseBody>

    @GET("api/getProductSection")
    fun getProductSections(): Call<ResponseBody>

    @GET("api/getDailyRecommendationInsights")
    fun getInsightRecommendations(): Call<ResponseBody>

    @GET("api/getInsightCategories")
    fun getInsightCategories(): Call<ResponseBody>

    @GET("api/getInsightByCategory")
    fun getInsightsWithCategory(
        @Query("id") id: Int,
        @Query("skip") skip: Int = 0,
        @Query("take") take: Int = AppController.PAGINATION_COUNT
    ): Call<ResponseBody>

    @GET("api/getInsightById/{id}")
    fun getInsightDetail(@Path("id") id: Int): Call<ResponseBody>

    @GET("api/insightLike/{id}")
    fun likeInsight(@Path("id") id: Int): Call<ResponseBody>

    @POST("api/storeBookmark")
    fun bookmarkThings(
        @Query("table") table: String,
        @Query("table_id") table_id: Int
    ): Call<ResponseBody>

    @POST("api/getTodayTasks")
    fun getTodayTasks(
        @Query("date") date: String, //09-23-2021
    ): Call<ResponseBody>

    @Multipart
    @POST("api/saveUserTodaysImage")
    fun saveUserImage(
        @Query("date") date: String, //09-19-2021
        @Part photo: MultipartBody.Part?,
    ): Call<ResponseBody>

    @POST("api/changeFoodStatus")
    fun changeFoodStatus(
        @Query("food_id") food_id: Int,
        @Query("status") status: String, //Skip, Done, Change
    ): Call<ResponseBody>

    @GET("api/getProfile")
    fun getProfile(): Call<ResponseBody>

    @Multipart
    @POST("api/updateUserProfile")
    fun updateProfile(
        @Part("name") name: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("date_of_birth") date_of_birth: RequestBody,
        @Part("height") height: RequestBody,
        @Part("weight") weight: RequestBody,
        @Part("user_height_unit") user_height_unit: RequestBody,
        @Part("user_weight_unit") user_weight_unit: RequestBody,
        @Part photo: MultipartBody.Part?,
    ): Call<ResponseBody>

    @Multipart
    @POST("api/updateUserProfile")
    fun updateProfileData(
        @Part("name") name: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("date_of_birth") date_of_birth: RequestBody,
        @Part("height") height: RequestBody,
        @Part("weight") weight: RequestBody,
        @Part("user_height_unit") user_height_unit: RequestBody,
        @Part("user_weight_unit") user_weight_unit: RequestBody,
    ): Call<ResponseBody>

    @GET("api/productGetById/{id}")
    fun getProductById(@Path("id") id: Int): Call<ResponseBody>

    @GET("api/getCartItems")
    fun getCartItems(): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/increaseQuantityOfCartItem")
    fun increaseQuantity(@Field("cart_id") cart_id: Int): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/decreaseQuantityOfCartItem")
    fun decreaseQuantity(@Field("cart_id") cart_id: Int): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/removeCartItem")
    fun removeCartItem(@Field("cart_id") cart_id: Int): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/addProductToCart")
    fun addProductToCart(
        @Field("product_id") product_id: Int,
        @Field("size_id") size_id: Int,
        @Field("color_id") color_id: Int,
        @Field("quantity") quantity: Int,
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/updateCartItem")
    fun updateCartItem(
        @Field("product_id") product_id: Int,
        @Field("size_id") size_id: Int,
        @Field("color_id") color_id: Int,
        @Field("quantity") quantity: Int,
        @Field("cart_id") cart_id: Int,
    ): Call<ResponseBody>

    @GET("api/userShippingAddresses")
    fun getUserShippingAddresses(): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/validatePromoCode")
    fun applyPromo(
        @Field("code") code: String,
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("api/addShippingAddress")
    fun addShippingAddress(
        @Field("street_address") street_address: String,
        @Field("city") city: String,
        @Field("state") state: String,
        @Field("country") country: String,
        @Field("zip_code") zip_code: String,
        @Field("phone_number") phone_number: String,
        @Field("is_default") is_default: Int,
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/updateShippingAddress")
    fun updateShippingAddress(
        @Field("shipping_address_id") shipping_address_id: Int,
        @Field("street_address") street_address: String,
        @Field("city") city: String,
        @Field("state") state: String,
        @Field("country") country: String,
        @Field("zip_code") zip_code: String,
        @Field("phone_number") phone_number: String,
        @Field("is_default") is_default: Int,
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/deleteShippingAddress")
    fun deleteShippingAddress(
        @Field("shipping_address_id") shipping_address_id: Int,
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/workoutFilterSearch")
    fun getWorkOutData(
        @Field("category_id") category_id: Int,
        @Field("level") level: Int,
        @Field("gender") gender: String,
        @Field("duration") duration: Int,
        @Field("search") search: String,
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("api/insightFilterSearch")
    fun getInsightFilterData(
        @Field("category_id") category_id: Int,
        @Field("duration") duration: Int,
        @Field("search") search: String,
    ): Call<ResponseBody>

    @POST("api/foodFilterSearch")
    fun getFoodFilterData(
        @Query("category_id") category_id: Int,
        @Query("ingredient_ids") ingredient_ids: Array<FoodFilterResultIngredient>,
        @Query("type") type: String,
        @Query("weight_status") weight_status: String,
        @Query("calories") calories: Int,
        @Query("search") search: String,
//        @Query("time") time: Int
    ): Call<ResponseBody>

    @GET("api/getWorkoutAvailableFilters")
    fun getWorkoutAvailableFilters(): Call<ResponseBody>

    @GET("api/getInsightAvailableFilters")
    fun getInsightAvailableFilters(): Call<ResponseBody>

    @GET("api/getFoodAvailableFilters")
    fun getFoodAvailableFilters(): Call<ResponseBody>

    @GET("api/getUserCards")
    fun getUserCards(): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/deleteUserCard")
    fun deleteUserCard(
        @Field("card_id") card_id: Int
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/createCard")
    fun addUserCard(
        @Field("token") token: String
    ): Call<ResponseBody>

    @GET("api/makeCardDefault/{id}")
    fun makeCardDefault(
        @Path("id") id: Int
    ): Call<ResponseBody>

    @GET("api/makeDefault/{id}")
    fun makeShippingAddressDefault(
        @Path("id") id: Int
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/updatePassword")
    fun changePassword(
        @Field("email") email: String,
        @Field("old_password") oldPassword: String,
        @Field("password") password: String,
        @Field("password_confirmation") password_confirmation: String,
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/searchFoodByName?skip=")
    fun simpleSearchFoodByName(
        @Field("search") search: String,
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/searchInsight?search=")
    fun simpleSearchInsightByName(
        @Field("search") search: String,
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/placeOrder")
    fun placeOrder(
        @Field("shipping_address_id") shipping_address_id: Int,
        @Field("card_id") card_id: Int
    ): Call<ResponseBody>

    @GET("api/getUserOrders")
    fun getUserOrders(
        @Query("skip") skip: Int,
        @Query("take") take: Int = AppController.PAGINATION_COUNT,
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/getBookmarksByType")
    fun getWorkoutBookmark(
        @Field("type") type: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/getAllFaqs?skip=0")
    fun getFaqs(
        @Field("skip") skip: Int
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/sendFeedback")
    fun sendFeedback(
        @Field("feedback") feedback: String,
        @Field("rating") rating: Int
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/openNotification")
    fun readNotification(
        @Field("notification_id") email: Int,
    ): Call<ResponseBody>

    @GET("api/getAllPodcasts")
    fun getAllPodcasts(): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/sendEmailInvitation")
    fun sendEmailInvitation(
        @Field("email") email: String,
        @Field("link") link: String = "http://www.brittany.com",
    ): Call<ResponseBody>
}





















