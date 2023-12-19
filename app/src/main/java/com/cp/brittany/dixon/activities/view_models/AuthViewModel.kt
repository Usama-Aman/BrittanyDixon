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

class AuthViewModel : ViewModel(), ResponseCallBack {

    private val apiResponse = MutableLiveData<Resource<JSONObject>>()

    /*Auth*/
    fun login(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.LOGIN)
        RetrofitClient.apiCall(call, this, ApiTags.LOGIN)
    }
    fun loginAsGuest(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GUEST_LOGIN)
        RetrofitClient.apiCall(call, this, ApiTags.GUEST_LOGIN)
    }
    fun register(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.REGISTER)
        RetrofitClient.apiCall(call, this, ApiTags.REGISTER)
    }
    fun registerGuest(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GUEST_REGISTER)
        RetrofitClient.apiCall(call, this, ApiTags.GUEST_REGISTER)
    }
    fun forgotPassword(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.FORGOT_PASSWORD)
        RetrofitClient.apiCall(call, this, ApiTags.FORGOT_PASSWORD)
    }

    fun createNewPassword(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.CREATE_NEW_PASSWORD)
        RetrofitClient.apiCall(call, this, ApiTags.CREATE_NEW_PASSWORD)
    }

    fun verifyOTP(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.VERIFY_OTP)
        RetrofitClient.apiCall(call, this, ApiTags.VERIFY_OTP)
    }

    fun resendOTP(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.RESEND_OTP)
        RetrofitClient.apiCall(call, this, ApiTags.RESEND_OTP)
    }

    fun socialLogin(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.SOCIAL_LOGIN)
        RetrofitClient.apiCall(call, this, ApiTags.SOCIAL_LOGIN)
    }

    fun assignPreference(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.ASSIGN_PREFERENCE)
        RetrofitClient.apiCall(call, this, ApiTags.ASSIGN_PREFERENCE)
    }

    fun getBanner(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_BANNER)
        RetrofitClient.apiCall(call, this, ApiTags.GET_BANNER)
    }
    //Preferences

    fun getWorkoutPreferences(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_WORKOUT_PREFERENCES)
        RetrofitClient.apiCall(call, this, ApiTags.GET_WORKOUT_PREFERENCES)
    }

    fun getDietaryPreferences(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.GET_DIETARY_PREFERENCES)
        RetrofitClient.apiCall(call, this, ApiTags.GET_DIETARY_PREFERENCES)
    }

    fun updatePassword(call: Call<ResponseBody>) {
        apiResponse.value = Resource.loading(ApiTags.UPDATE_PASSWORD)
        RetrofitClient.apiCall(call, this, ApiTags.UPDATE_PASSWORD)
    }





    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        when (tag) {
            ApiTags.LOGIN -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.LOGIN))
            }
            ApiTags.GUEST_LOGIN -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GUEST_LOGIN))
            }
            ApiTags.REGISTER -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.REGISTER))
            }
            ApiTags.GUEST_REGISTER -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GUEST_REGISTER))
            }
            ApiTags.FORGOT_PASSWORD -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.FORGOT_PASSWORD))
            }
            ApiTags.CREATE_NEW_PASSWORD -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.CREATE_NEW_PASSWORD))
            }
            ApiTags.VERIFY_OTP -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.VERIFY_OTP))
            }
            ApiTags.RESEND_OTP -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.RESEND_OTP))
            }
            ApiTags.SOCIAL_LOGIN -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.SOCIAL_LOGIN))
            }
            ApiTags.GET_WORKOUT_PREFERENCES -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_WORKOUT_PREFERENCES))
            }
            ApiTags.GET_DIETARY_PREFERENCES -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_DIETARY_PREFERENCES))
            }
            ApiTags.ASSIGN_PREFERENCE -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.ASSIGN_PREFERENCE))
            }
            ApiTags.UPDATE_PASSWORD -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.UPDATE_PASSWORD))
            }
            ApiTags.GET_BANNER -> {
                apiResponse.postValue(Resource.success(jsonObject, ApiTags.GET_BANNER))
            }

        }
    }

    override fun onError(error: String, tag: String) {
        when (tag) {
            ApiTags.LOGIN -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.LOGIN))
            }
            ApiTags.GUEST_LOGIN -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GUEST_LOGIN))
            }
            ApiTags.REGISTER -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.REGISTER))
            }
            ApiTags.GUEST_REGISTER -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GUEST_REGISTER))
            }
            ApiTags.FORGOT_PASSWORD -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.FORGOT_PASSWORD))
            }
            ApiTags.CREATE_NEW_PASSWORD -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.CREATE_NEW_PASSWORD))
            }
            ApiTags.VERIFY_OTP -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.VERIFY_OTP))
            }
            ApiTags.RESEND_OTP -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.RESEND_OTP))
            }
            ApiTags.SOCIAL_LOGIN -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.SOCIAL_LOGIN))
            }
            ApiTags.GET_WORKOUT_PREFERENCES -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_WORKOUT_PREFERENCES))
            }
            ApiTags.GET_DIETARY_PREFERENCES -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_DIETARY_PREFERENCES))
            }
            ApiTags.ASSIGN_PREFERENCE -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.ASSIGN_PREFERENCE))
            }
            ApiTags.UPDATE_PASSWORD -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.UPDATE_PASSWORD))
            }
            ApiTags.GET_BANNER -> {
                apiResponse.postValue(Resource.error(error, null, ApiTags.GET_BANNER))
            }
        }
    }

    fun getApiResponse(): LiveData<Resource<JSONObject>> = apiResponse
}