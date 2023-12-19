package com.cp.brittany.dixon.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.auth.models.UserDetailData
import com.google.gson.Gson
import com.tapadoo.alerter.Alerter
import java.text.SimpleDateFormat
import java.util.*


object AppUtils {


    fun hideKeyBoardFromEditText(editText: EditText, context: Context) {
        val imm: InputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    fun showToast(activity: Activity, text: String, isSuccess: Boolean) {
        if (isSuccess) {
            Alerter.create(activity)
                .setTitle(activity.resources.getString(R.string.toast_success))
                .setTitleTypeface(Typeface.createFromAsset(activity.assets, "roboto_bold.ttf"))
                .setTextTypeface(Typeface.createFromAsset(activity.assets, "roboto_regular.ttf"))
                .setText(text)
                .setIcon(R.drawable.ic_toast_success)
                .setBackgroundColorRes(R.color.bg_btn_main)
                .setDuration(1000)
                .show()
        } else {
            Alerter.create(activity)
                .setTitle(activity.resources.getString(R.string.toast_error))
                .setTitleTypeface(Typeface.createFromAsset(activity.assets, "roboto_bold.ttf"))
                .setTextTypeface(Typeface.createFromAsset(activity.assets, "roboto_regular.ttf"))
                .setText(text)
                .setIcon(R.drawable.ic_toast_error)
                .setBackgroundColorRes(R.color.red)
                .setDuration(1000)
                .show()
        }
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return (netInfo != null && netInfo.isConnectedOrConnecting
                && cm.activeNetworkInfo!!.isAvailable
                && cm.activeNetworkInfo!!.isConnected)
    }

    fun preventDoubleClick(view: View) {
        view.isEnabled = false
        Handler(Looper.getMainLooper()).postDelayed({
            view.isEnabled = true
        }, 200)
    }

    fun touchScreenEnableDisable(context: Context, isTouchEnable: Boolean) {
        if (isTouchEnable)
            (context as Activity).window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        else
            (context as Activity).window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
    }

    fun getUserDetails(context: Context): UserDetailData {
        val data = SharedPreference.getSimpleString(context, Constants.userModel)
        return Gson().fromJson(data, UserDetailData::class.java)
    }

    fun saveUserModel(mContext: Context, data: UserDetailData) {
        SharedPreference.saveString(mContext, Constants.userModel, Gson().toJson(data))
    }

    @SuppressLint("SimpleDateFormat")
    fun convertToCustomFormat(dateStr: String): String {
        var date = dateStr
        var spf = SimpleDateFormat("EEE MMM dd hh:mm:ss zzzz yyyy")
        val newDate = spf.parse(date)
        spf = SimpleDateFormat("MM-dd-yyyy")
        date = spf.format(newDate)
        println(date)
        return date
    }

}