package com.cp.brittany.dixon.network

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.cp.brittany.dixon.BuildConfig
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.utils.AppUtils
import com.cp.brittany.dixon.utils.Constants
import com.cp.brittany.dixon.utils.SharedPreference
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@SuppressLint("StaticFieldLeak")
object RetrofitClient {

    private const val BASE_URL = "http://178.128.29.7/brittney-v02/"
    private lateinit var context: Context

    fun getClient(context: Context): Retrofit {

        val token = SharedPreference.getSimpleString(context, Constants.accessToken)
        RetrofitClient.context = context

        val interceptor = HttpLoggingInterceptor()
        interceptor.level =
            if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE


        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .addInterceptor { chain ->
                val newRequest: Request = chain.request().newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader(
                        "Authorization",
                        "Bearer $token"
                    )
                    .build()
                chain.proceed(newRequest)
            }.build()

        return Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getClientNoToken(context: Context): Retrofit {
        RetrofitClient.context = context

        val interceptor = HttpLoggingInterceptor()
        interceptor.level =
            if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE


        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .addInterceptor { chain ->
                val newRequest: Request = chain.request().newBuilder()
                    .addHeader("Accept", "application/json")
                    .build()
                chain.proceed(newRequest)
            }.build()

        return Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun apiCall(call: Call<ResponseBody>, responseCallBack: ResponseCallBack, tag: String) {

        if (!AppUtils.isNetworkAvailable(context)) {
            responseCallBack.onError(
                context.resources.getString(R.string.no_internet_connection),
                tag
            )
            return
        }

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                try {
                    if (response.isSuccessful) {
                        responseCallBack.onSuccess(JSONObject(response.body()!!.string()), tag)
                    } else {
                        println("Exception in api $response")
                        val errorJSONObject = JSONObject(response.errorBody()!!.string())
                        responseCallBack.onError(errorJSONObject.getString("message"), tag)
                        Log.d("Exception", errorJSONObject.getString("message"))

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("Response Failure", t.localizedMessage!!)
                if (!call.isCanceled)
                    responseCallBack.onError(t.localizedMessage!!, tag)
            }

        })
    }

}