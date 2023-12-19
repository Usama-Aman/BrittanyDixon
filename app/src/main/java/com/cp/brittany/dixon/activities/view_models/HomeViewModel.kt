package com.cp.brittany.dixon.activities.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.ResponseCallBack
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.Resource
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call

class HomeViewModel : ViewModel(), ResponseCallBack {

    private val apiResponse = MutableLiveData<Resource<JSONObject>>()

    fun logout(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.LOGOUT)
        RetrofitClient.apiCall(call, this, ApiTags.LOGOUT)
    }

    fun getBanner(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_BANNER)
        RetrofitClient.apiCall(call, this, ApiTags.GET_BANNER)
    }

    fun getCountries(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_COUNTRIES)
        RetrofitClient.apiCall(call, this, ApiTags.GET_COUNTRIES)
    }

    fun getStates(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_STATES)
        RetrofitClient.apiCall(call, this, ApiTags.GET_STATES)
    }

    fun getCities(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_CITIES)
        RetrofitClient.apiCall(call, this, ApiTags.GET_CITIES)
    }

    fun getNotifications(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_NOTIFICATIONS)
        RetrofitClient.apiCall(call, this, ApiTags.GET_NOTIFICATIONS)
    }

    fun getAllWorkouts(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_ALL_WORKOUTS)
        RetrofitClient.apiCall(call, this, ApiTags.GET_ALL_WORKOUTS)
    }

    fun getWorkoutDetails(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_WORKOUT_DETAILS)
        RetrofitClient.apiCall(call, this, ApiTags.GET_WORKOUT_DETAILS)
    }

    fun resetWorkout(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.RESET_WORKOUT)
        RetrofitClient.apiCall(call, this, ApiTags.RESET_WORKOUT)
    }

    fun getFoodDetails(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_FOOD_DETAILS)
        RetrofitClient.apiCall(call, this, ApiTags.GET_FOOD_DETAILS)
    }

    fun getWorkoutLessons(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_WORKOUT_LESSONS)
        RetrofitClient.apiCall(call, this, ApiTags.GET_WORKOUT_LESSONS)
    }

    fun getAllFoods(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_ALL_FOODS)
        RetrofitClient.apiCall(call, this, ApiTags.GET_ALL_FOODS)
    }

    fun getFoodByType(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_FOOD_BY_TYPE)
        RetrofitClient.apiCall(call, this, ApiTags.GET_FOOD_BY_TYPE)
    }

    fun addRemoveFoodToUserFoods(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.ADD_REMOVE_FOOD_TO_USER_FOODS)
        RetrofitClient.apiCall(call, this, ApiTags.ADD_REMOVE_FOOD_TO_USER_FOODS)
    }

    fun getFeaturedFoodByType(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_FEATURED_FOOD_BY_TYPE)
        RetrofitClient.apiCall(call, this, ApiTags.GET_FEATURED_FOOD_BY_TYPE)
    }

    fun foodSimpleSearch(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.FOOD_SIMPLE_SEARCH)
        RetrofitClient.apiCall(call, this, ApiTags.FOOD_SIMPLE_SEARCH)
    }

    fun getProductCategories(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_PRODUCT_CATEGORIES)
        RetrofitClient.apiCall(call, this, ApiTags.GET_PRODUCT_CATEGORIES)
    }

    fun getNewArrivals(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_NEW_ARRIVALS)
        RetrofitClient.apiCall(call, this, ApiTags.GET_NEW_ARRIVALS)
    }

    fun getSectionProductsById(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_SECTION_PRODUCTS)
        RetrofitClient.apiCall(call, this, ApiTags.GET_SECTION_PRODUCTS)
    }

    fun getTrendingProduct(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_TRENDING_PRODUCTS)
        RetrofitClient.apiCall(call, this, ApiTags.GET_TRENDING_PRODUCTS)
    }

    fun getHistoryProducts(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_HISTORY_PRODUCTS)
        RetrofitClient.apiCall(call, this, ApiTags.GET_HISTORY_PRODUCTS)
    }

    fun getProductSections(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_PRODUCTS_SECTIONS)
        RetrofitClient.apiCall(call, this, ApiTags.GET_PRODUCTS_SECTIONS)
    }

    fun getInsightRecommendations(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_INSIGHT_RECOMMENDATIONS)
        RetrofitClient.apiCall(call, this, ApiTags.GET_INSIGHT_RECOMMENDATIONS)
    }

    fun getInsightCategories(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_INSIGHT_CATEGORIES)
        RetrofitClient.apiCall(call, this, ApiTags.GET_INSIGHT_CATEGORIES)
    }

    fun getInsightsWithCategory(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_INSIGHT_WITH_CATEGORY)
        RetrofitClient.apiCall(call, this, ApiTags.GET_INSIGHT_WITH_CATEGORY)
    }

    fun getInsightDetail(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_INSIGHT_DETAIL)
        RetrofitClient.apiCall(call, this, ApiTags.GET_INSIGHT_DETAIL)
    }

    fun likeInsight(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.INSIGHT_LIKE)
        RetrofitClient.apiCall(call, this, ApiTags.INSIGHT_LIKE)
    }

    fun bookmarkThings(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.BOOKMARK_THINGS)
        RetrofitClient.apiCall(call, this, ApiTags.BOOKMARK_THINGS)
    }

    fun getTodayTasks(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_TODAY_TASKS)
        RetrofitClient.apiCall(call, this, ApiTags.GET_TODAY_TASKS)
    }

    fun saveUserImage(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.SAVE_USER_IMAGE)
        RetrofitClient.apiCall(call, this, ApiTags.SAVE_USER_IMAGE)
    }

    fun changeFoodStatus(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.CHANGE_FOOD_STATUS)
        RetrofitClient.apiCall(call, this, ApiTags.CHANGE_FOOD_STATUS)
    }

    fun getProfile(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_PROFILE)
        RetrofitClient.apiCall(call, this, ApiTags.GET_PROFILE)
    }

    fun updateProfile(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.UPDATE_PROFILE)
        RetrofitClient.apiCall(call, this, ApiTags.UPDATE_PROFILE)
    }

    fun getProductById(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_PRODUCT_BY_ID)
        RetrofitClient.apiCall(call, this, ApiTags.GET_PRODUCT_BY_ID)
    }

    fun getCartItems(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_CART_ITEMS)
        RetrofitClient.apiCall(call, this, ApiTags.GET_CART_ITEMS)
    }

    fun increaseQuantity(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.INCREASE_QUANTITY)
        RetrofitClient.apiCall(call, this, ApiTags.INCREASE_QUANTITY)
    }

    fun decreaseQuantity(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.DECREASE_QUANTITY)
        RetrofitClient.apiCall(call, this, ApiTags.DECREASE_QUANTITY)
    }

    fun addProductToCart(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.ADD_PRODUCT_TO_CART)
        RetrofitClient.apiCall(call, this, ApiTags.ADD_PRODUCT_TO_CART)
    }

    fun removeCartItem(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.REMOVE_CART_ITEM)
        RetrofitClient.apiCall(call, this, ApiTags.REMOVE_CART_ITEM)
    }

    fun getUserShippingAddresses(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_SHIPPING_ADDRESSES)
        RetrofitClient.apiCall(call, this, ApiTags.GET_SHIPPING_ADDRESSES)
    }

    fun applyPromo(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.APPLY_PROMO_CODE)
        RetrofitClient.apiCall(call, this, ApiTags.APPLY_PROMO_CODE)
    }

    fun addShippingAddress(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.ADD_SHIPPING_ADDRESS)
        RetrofitClient.apiCall(call, this, ApiTags.ADD_SHIPPING_ADDRESS)
    }

    fun deleteShippingAddress(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.DELETE_SHIPPING_ADDRESS)
        RetrofitClient.apiCall(call, this, ApiTags.DELETE_SHIPPING_ADDRESS)
    }

    fun getWorkoutSearchFilter(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.SEARCH_WORKOUT_FILTER)
        RetrofitClient.apiCall(call, this, ApiTags.SEARCH_WORKOUT_FILTER)
    }

    fun getInsightSearchFilter(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.SEARCH_INSIGHT_FILTER)
        RetrofitClient.apiCall(call, this, ApiTags.SEARCH_INSIGHT_FILTER)
    }

    fun getFoodSearchFilter(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.SEARCH_FOOD_FILTER)
        RetrofitClient.apiCall(call, this, ApiTags.SEARCH_FOOD_FILTER)
    }

    fun getWorkoutAvailableFilters(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_WORKOUT_FILTERS)
        RetrofitClient.apiCall(call, this, ApiTags.GET_WORKOUT_FILTERS)
    }


    fun getPaymentCards(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_PAYMENT_CARDS)
        RetrofitClient.apiCall(call, this, ApiTags.GET_PAYMENT_CARDS)
    }

    fun deletePaymentCard(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.DELETE_PAYMENT_CARD)
        RetrofitClient.apiCall(call, this, ApiTags.DELETE_PAYMENT_CARD)
    }

    fun addUserCard(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.ADD_PAYMENT_CARD)
        RetrofitClient.apiCall(call, this, ApiTags.ADD_PAYMENT_CARD)
    }

    fun makeCardDefault(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.Make_CARD_Default)
        RetrofitClient.apiCall(call, this, ApiTags.Make_CARD_Default)
    }

    fun makeShippingAddressDefault(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.Make_SHIPPING_ADDRESSES_Default)
        RetrofitClient.apiCall(call, this, ApiTags.Make_SHIPPING_ADDRESSES_Default)
    }

    fun getInsightAvailableFilters(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_INSIGHT_FILTERS)
        RetrofitClient.apiCall(call, this, ApiTags.GET_INSIGHT_FILTERS)
    }

    fun getFoodAvailableFilters(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_FOOD_FILTERS)
        RetrofitClient.apiCall(call, this, ApiTags.GET_FOOD_FILTERS)
    }

    fun updateShippingAddress(call: Call<ResponseBody>) {

        apiResponse.value = Resource.loading(ApiTags.UPDATE_SHIPPING_ADDRESSES)
        RetrofitClient.apiCall(call, this, ApiTags.UPDATE_SHIPPING_ADDRESSES)
    }

    fun simpleSearchFoodByName(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.FOOD_SIMPLE_SEARCH)
        RetrofitClient.apiCall(call, this, ApiTags.FOOD_SIMPLE_SEARCH)
    }

    fun simpleSearchInsightByName(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.SIMPLE_SEARCH_INSIGHT_BY_NAME)
        RetrofitClient.apiCall(call, this, ApiTags.SIMPLE_SEARCH_INSIGHT_BY_NAME)
    }

    fun placeOrder(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.PLACE_ORDER)
        RetrofitClient.apiCall(call, this, ApiTags.PLACE_ORDER)
    }

    fun getWorkoutBookmarks(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_BOOKMARK)
        RetrofitClient.apiCall(call, this, ApiTags.GET_BOOKMARK)
    }

    fun getFaqs(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_FAQS)
        RetrofitClient.apiCall(call, this, ApiTags.GET_FAQS)
    }

    fun sendFeedback(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.SEND_FEEDBACK)
        RetrofitClient.apiCall(call, this, ApiTags.SEND_FEEDBACK)
    }


    fun getUserOrders(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_USER_ORDERS)
        RetrofitClient.apiCall(call, this, ApiTags.GET_USER_ORDERS)
    }

    fun readNotification(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.READ_NOTIFICATION)
        RetrofitClient.apiCall(call, this, ApiTags.READ_NOTIFICATION)
    }

    fun getAllPodcasts(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_ALL_PODCASTS)
        RetrofitClient.apiCall(call, this, ApiTags.GET_ALL_PODCASTS)
    }

    fun sendEmailInvitation(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.EMAIL_INVITATION)
        RetrofitClient.apiCall(call, this, ApiTags.EMAIL_INVITATION)
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        when (tag) {
            ApiTags.GET_COUNTRIES -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_COUNTRIES))
            }
            ApiTags.GET_STATES -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_STATES))
            }
            ApiTags.GET_CITIES -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_CITIES))
            }
            ApiTags.GET_NOTIFICATIONS -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_NOTIFICATIONS))
            }
            ApiTags.GET_ALL_WORKOUTS -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_ALL_WORKOUTS))
            }
            ApiTags.GET_WORKOUT_DETAILS -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_WORKOUT_DETAILS))
            }
            ApiTags.GET_WORKOUT_LESSONS -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_WORKOUT_LESSONS))
            }
            ApiTags.GET_ALL_FOODS -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_ALL_FOODS))
            }
            ApiTags.GET_FOOD_BY_TYPE -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_FOOD_BY_TYPE))
            }
            ApiTags.FOOD_SIMPLE_SEARCH -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.FOOD_SIMPLE_SEARCH))
            }
            ApiTags.GET_FOOD_DETAILS -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_FOOD_DETAILS))
            }
            ApiTags.GET_PRODUCT_CATEGORIES -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_PRODUCT_CATEGORIES))
            }
            ApiTags.GET_NEW_ARRIVALS -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_NEW_ARRIVALS))
            }
            ApiTags.GET_TRENDING_PRODUCTS -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_TRENDING_PRODUCTS))
            }
            ApiTags.GET_HISTORY_PRODUCTS -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_HISTORY_PRODUCTS))
            }
            ApiTags.GET_PRODUCTS_SECTIONS -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_PRODUCTS_SECTIONS))
            }
            ApiTags.GET_INSIGHT_RECOMMENDATIONS -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_INSIGHT_RECOMMENDATIONS))
            }
            ApiTags.GET_INSIGHT_CATEGORIES -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_INSIGHT_CATEGORIES))
            }
            ApiTags.GET_INSIGHT_WITH_CATEGORY -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_INSIGHT_WITH_CATEGORY))
            }
            ApiTags.GET_INSIGHT_DETAIL -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_INSIGHT_DETAIL))
            }
            ApiTags.INSIGHT_LIKE -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.INSIGHT_LIKE))
            }
            ApiTags.BOOKMARK_THINGS -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.BOOKMARK_THINGS))
            }
            ApiTags.GET_TODAY_TASKS -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_TODAY_TASKS))
            }
            ApiTags.CHANGE_FOOD_STATUS -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.CHANGE_FOOD_STATUS))
            }
            ApiTags.GET_PROFILE -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_PROFILE))
            }
            ApiTags.UPDATE_PROFILE -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.UPDATE_PROFILE))
            }
            ApiTags.GET_PRODUCT_BY_ID -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_PRODUCT_BY_ID))
            }
            ApiTags.GET_CART_ITEMS -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_CART_ITEMS))
            }
            ApiTags.INCREASE_QUANTITY -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.INCREASE_QUANTITY))
            }
            ApiTags.DECREASE_QUANTITY -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.DECREASE_QUANTITY))
            }
            ApiTags.ADD_PRODUCT_TO_CART -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.ADD_PRODUCT_TO_CART))
            }
            ApiTags.REMOVE_CART_ITEM -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.REMOVE_CART_ITEM))
            }
            ApiTags.GET_SHIPPING_ADDRESSES -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_SHIPPING_ADDRESSES))
            }
            ApiTags.APPLY_PROMO_CODE -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.APPLY_PROMO_CODE))
            }
            ApiTags.ADD_SHIPPING_ADDRESS -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.ADD_SHIPPING_ADDRESS))
            }
            ApiTags.DELETE_SHIPPING_ADDRESS -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.DELETE_SHIPPING_ADDRESS))
            }
            ApiTags.SEARCH_WORKOUT_FILTER -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.SEARCH_WORKOUT_FILTER))
            }
            ApiTags.SEARCH_INSIGHT_FILTER -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.SEARCH_INSIGHT_FILTER))
            }
            ApiTags.SEARCH_FOOD_FILTER -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.SEARCH_FOOD_FILTER))
            }
            ApiTags.GET_WORKOUT_FILTERS -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_WORKOUT_FILTERS))
            }
            ApiTags.GET_PAYMENT_CARDS -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_PAYMENT_CARDS))
            }
            ApiTags.DELETE_PAYMENT_CARD -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.DELETE_PAYMENT_CARD))
            }
            ApiTags.Make_CARD_Default -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.Make_CARD_Default))
            }
            ApiTags.Make_SHIPPING_ADDRESSES_Default -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.Make_SHIPPING_ADDRESSES_Default))
            }
            ApiTags.GET_INSIGHT_FILTERS -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_INSIGHT_FILTERS))
            }
            ApiTags.GET_FOOD_FILTERS -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_FOOD_FILTERS))
            }

            ApiTags.UPDATE_SHIPPING_ADDRESSES -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.UPDATE_SHIPPING_ADDRESSES))
            }
            ApiTags.SIMPLE_SEARCH_INSIGHT_BY_NAME -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.SIMPLE_SEARCH_INSIGHT_BY_NAME))
            }

            ApiTags.ADD_PAYMENT_CARD -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.ADD_PAYMENT_CARD))
            }
            ApiTags.PLACE_ORDER -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.PLACE_ORDER))
            }
            ApiTags.GET_USER_ORDERS -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_USER_ORDERS))
            }
            ApiTags.GET_BOOKMARK -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_BOOKMARK))
            }
            ApiTags.GET_FAQS -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_FAQS))
            }
            ApiTags.SEND_FEEDBACK -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.SEND_FEEDBACK))
            }
            ApiTags.SAVE_USER_IMAGE -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.SAVE_USER_IMAGE))
            }
            ApiTags.READ_NOTIFICATION -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.READ_NOTIFICATION))
            }
            ApiTags.GET_FEATURED_FOOD_BY_TYPE-> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_FEATURED_FOOD_BY_TYPE))
            }
            ApiTags.ADD_REMOVE_FOOD_TO_USER_FOODS-> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.ADD_REMOVE_FOOD_TO_USER_FOODS))
            }
            ApiTags.GET_SECTION_PRODUCTS-> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_SECTION_PRODUCTS))
            }
            ApiTags.GET_ALL_PODCASTS-> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_ALL_PODCASTS))
            }
            ApiTags.RESET_WORKOUT-> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.RESET_WORKOUT))
            }
            ApiTags.LOGOUT-> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.LOGOUT))
            }
            ApiTags.GET_BANNER-> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_BANNER))
            }
            ApiTags.EMAIL_INVITATION-> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.EMAIL_INVITATION))
            }
        }
    }

    override fun onError(error: String, tag: String) {
        when (tag) {
            ApiTags.GET_COUNTRIES -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_COUNTRIES))
            }
            ApiTags.GET_STATES -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_STATES))
            }
            ApiTags.GET_CITIES -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_CITIES))
            }
            ApiTags.GET_NOTIFICATIONS -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_NOTIFICATIONS))
            }
            ApiTags.GET_ALL_WORKOUTS -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_ALL_WORKOUTS))
            }
            ApiTags.GET_WORKOUT_DETAILS -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_WORKOUT_DETAILS))
            }
            ApiTags.GET_WORKOUT_LESSONS -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_WORKOUT_LESSONS))
            }
            ApiTags.GET_ALL_FOODS -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_ALL_FOODS))
            }
            ApiTags.GET_FOOD_BY_TYPE -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_FOOD_BY_TYPE))
            }
            ApiTags.FOOD_SIMPLE_SEARCH -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.FOOD_SIMPLE_SEARCH))
            }
            ApiTags.GET_FOOD_DETAILS -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_FOOD_DETAILS))
            }
            ApiTags.GET_PRODUCT_CATEGORIES -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_PRODUCT_CATEGORIES))
            }
            ApiTags.GET_NEW_ARRIVALS -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_NEW_ARRIVALS))
            }
            ApiTags.GET_TRENDING_PRODUCTS -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_TRENDING_PRODUCTS))
            }
            ApiTags.GET_HISTORY_PRODUCTS -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_HISTORY_PRODUCTS))
            }
            ApiTags.GET_PRODUCTS_SECTIONS -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_PRODUCTS_SECTIONS))
            }
            ApiTags.GET_INSIGHT_RECOMMENDATIONS -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_INSIGHT_RECOMMENDATIONS))
            }
            ApiTags.GET_INSIGHT_CATEGORIES -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_INSIGHT_CATEGORIES))
            }
            ApiTags.GET_INSIGHT_WITH_CATEGORY -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_INSIGHT_WITH_CATEGORY))
            }
            ApiTags.GET_INSIGHT_DETAIL -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_INSIGHT_DETAIL))
            }
            ApiTags.INSIGHT_LIKE -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.INSIGHT_LIKE))
            }
            ApiTags.BOOKMARK_THINGS -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.BOOKMARK_THINGS))
            }
            ApiTags.GET_TODAY_TASKS -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_TODAY_TASKS))
            }
            ApiTags.CHANGE_FOOD_STATUS -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.CHANGE_FOOD_STATUS))
            }
            ApiTags.GET_PROFILE -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_PROFILE))
            }
            ApiTags.UPDATE_PROFILE -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.UPDATE_PROFILE))
            }
            ApiTags.GET_PRODUCT_BY_ID -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_PRODUCT_BY_ID))
            }
            ApiTags.GET_CART_ITEMS -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_CART_ITEMS))
            }
            ApiTags.INCREASE_QUANTITY -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.INCREASE_QUANTITY))
            }
            ApiTags.DECREASE_QUANTITY -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.DECREASE_QUANTITY))
            }
            ApiTags.ADD_PRODUCT_TO_CART -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.ADD_PRODUCT_TO_CART))
            }
            ApiTags.REMOVE_CART_ITEM -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.REMOVE_CART_ITEM))
            }
            ApiTags.GET_SHIPPING_ADDRESSES -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_SHIPPING_ADDRESSES))
            }
            ApiTags.APPLY_PROMO_CODE -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.APPLY_PROMO_CODE))
            }
            ApiTags.ADD_SHIPPING_ADDRESS -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.ADD_SHIPPING_ADDRESS))
            }
            ApiTags.DELETE_SHIPPING_ADDRESS -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.DELETE_SHIPPING_ADDRESS))
            }
            ApiTags.SEARCH_WORKOUT_FILTER -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.SEARCH_WORKOUT_FILTER))
            }
            ApiTags.SEARCH_INSIGHT_FILTER -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.SEARCH_INSIGHT_FILTER))
            }
            ApiTags.SEARCH_FOOD_FILTER -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.SEARCH_FOOD_FILTER))
            }
            ApiTags.GET_WORKOUT_FILTERS -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_WORKOUT_FILTERS))
            }
            ApiTags.GET_PAYMENT_CARDS -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_PAYMENT_CARDS))
            }
            ApiTags.DELETE_PAYMENT_CARD -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.DELETE_PAYMENT_CARD))
            }
            ApiTags.Make_SHIPPING_ADDRESSES_Default -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.Make_SHIPPING_ADDRESSES_Default))
            }
            ApiTags.GET_INSIGHT_FILTERS -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_INSIGHT_FILTERS))
            }
            ApiTags.GET_FOOD_FILTERS -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_FOOD_FILTERS))
            }
            ApiTags.UPDATE_SHIPPING_ADDRESSES -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.UPDATE_SHIPPING_ADDRESSES))
            }

            ApiTags.SIMPLE_SEARCH_INSIGHT_BY_NAME -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.SIMPLE_SEARCH_INSIGHT_BY_NAME))
            }
            ApiTags.ADD_PAYMENT_CARD -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.ADD_PAYMENT_CARD))
            }
            ApiTags.PLACE_ORDER -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.PLACE_ORDER))
            }
            ApiTags.GET_USER_ORDERS -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_USER_ORDERS))
            }
            ApiTags.GET_BOOKMARK -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_BOOKMARK))
            }
            ApiTags.GET_FAQS -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_FAQS))
            }
            ApiTags.SEND_FEEDBACK -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.SEND_FEEDBACK))
            }
            ApiTags.SAVE_USER_IMAGE -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.SAVE_USER_IMAGE))
            }
            ApiTags.READ_NOTIFICATION-> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.READ_NOTIFICATION))
            }
            ApiTags.GET_FEATURED_FOOD_BY_TYPE-> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_FEATURED_FOOD_BY_TYPE))
            }
            ApiTags.ADD_REMOVE_FOOD_TO_USER_FOODS-> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.ADD_REMOVE_FOOD_TO_USER_FOODS))
            }
            ApiTags.GET_SECTION_PRODUCTS-> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_SECTION_PRODUCTS))
            }
            ApiTags.GET_ALL_PODCASTS-> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_ALL_PODCASTS))
            }
            ApiTags.RESET_WORKOUT-> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.RESET_WORKOUT))
            }
            ApiTags.LOGOUT-> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.LOGOUT))
            }
            ApiTags.GET_BANNER-> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_BANNER))
            }
            ApiTags.EMAIL_INVITATION-> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.EMAIL_INVITATION))
            }
        }

    }

    fun getApiResponse(): LiveData<Resource<JSONObject>> = apiResponse

}
