package com.cp.brittany.dixon.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.cp.brittany.dixon.R
import com.google.android.material.textfield.TextInputLayout
import es.dmoral.toasty.Toasty
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

/*Functions of toast*/
fun showSuccessToast(context: Context, message: String) {
    Toasty.success(context, "" + message, Toast.LENGTH_SHORT, true).show();
}

fun showErrorToast(context: Context, message: String) {
    var toasty: Toast? = null
    var t = Toasty.error(context, "" + message, Toast.LENGTH_SHORT, true)
    (t as Toast).setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 40)
    t.show()
}

fun showInfoToast(context: Context, message: String) {
    Toasty.error(context, "" + message, Toast.LENGTH_SHORT, true).show();
}

/*Functions Related to Views*/

fun View.viewVisible() {
    this.visibility = View.VISIBLE
}

fun View.viewGone() {
    this.visibility = View.GONE
}

fun View.viewInVisible() {
    this.visibility = View.INVISIBLE
}

fun View.viewVisibility(): Int {
    return this.visibility
}

fun View.isVisible(): Boolean {
    return this.visibility == View.VISIBLE
}

fun View.isGone(): Boolean {
    return this.visibility == View.GONE
}

fun View.isInVisible(): Boolean {
    return this.visibility == View.INVISIBLE
}

//Hide show password
fun hideShowPassword(editText: EditText, toggle: ImageView) {
    if (editText.transformationMethod.equals(PasswordTransformationMethod.getInstance())) {
        toggle.setImageResource(R.drawable.ic_hide_password);
        editText.transformationMethod = HideReturnsTransformationMethod.getInstance();
    } else {
        toggle.setImageResource(R.drawable.ic_show_password);
        editText.transformationMethod = PasswordTransformationMethod.getInstance();
    }
    editText.setSelection(editText.text.toString().length)
}

fun dpToPx(dp: Int, context: Context): Int {
    val density = context.resources.displayMetrics.density
    return (dp.toFloat() * density).roundToInt()
}

//1 minute = 60 seconds
//1 hour = 60 x 60 = 3600
//1 day = 3600 x 24 = 86400
@SuppressLint("SimpleDateFormat")
fun printDifference(endDate: String) {
    //milliseconds
    val today = Date()
    val endingDate: Date = SimpleDateFormat("dd-M-yyyy").parse(endDate)
    var different = endingDate.time - today.time
    println("startDate : $today")
    println("endDate : $endDate")
    println("different : $different")
    val secondsInMilli: Long = 1000
    val minutesInMilli = secondsInMilli * 60
    val hoursInMilli = minutesInMilli * 60
    val daysInMilli = hoursInMilli * 24
    val elapsedDays = different / daysInMilli
    different %= daysInMilli
    val elapsedHours = different / hoursInMilli
    different %= hoursInMilli
    val elapsedMinutes = different / minutesInMilli
    different %= minutesInMilli
    val elapsedSeconds = different / secondsInMilli
    System.out.printf(
        "%d days, %d hours, %d minutes, %d seconds%n",
        elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds
    )
}

/*Functions Related to Validations*/
fun String.isValidEmail(): Boolean =
    this.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.isValidMobileNumber(): Boolean =
    this.isNotEmpty() && Patterns.PHONE.matcher(this).matches()


@SuppressLint("MissingPermission")
@Suppress("DEPRECATION")
fun isInternetAvailable(context: Context): Boolean {
    var result = false
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        cm?.run {
            cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                result = when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            }
        }
    } else {
        cm?.run {
            cm.activeNetworkInfo?.run {
                if (type == ConnectivityManager.TYPE_WIFI) {
                    result = true
                } else if (type == ConnectivityManager.TYPE_MOBILE) {
                    result = true
                }
            }
        }
    }
    return result
}

fun focusIn(et: EditText, src: Int, ctx: Context) {
    var drawable: Drawable? = ContextCompat.getDrawable(ctx, src)
    drawable?.colorFilter = PorterDuffColorFilter(
        ContextCompat.getColor(ctx, R.color.white), PorterDuff.Mode.SRC_IN
    )
    et.setCompoundDrawablesWithIntrinsicBounds(
        drawable,
        null,
        null,
        null
    )
}

fun focusInTil(et: TextInputLayout, src: Int, ctx: Context) {
    var drawable: Drawable? = ContextCompat.getDrawable(ctx, src)
    drawable?.colorFilter = PorterDuffColorFilter(
        ContextCompat.getColor(ctx, R.color.white), PorterDuff.Mode.SRC_IN
    )

    et.startIconDrawable = drawable
}

fun focusOut(et: EditText, src: Int, ctx: Context) {
    var drawable: Drawable? = ContextCompat.getDrawable(ctx, src)
    drawable = DrawableCompat.wrap(drawable!!)
    et.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
}

fun focusOutTil(et: TextInputLayout, src: Int, ctx: Context) {
    var drawable: Drawable? = ContextCompat.getDrawable(ctx, src)
    drawable = DrawableCompat.wrap(drawable!!)
    et.startIconDrawable = drawable
}

fun convertDateFormat(date_string: String): String {
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    var myDate: Date? = null
    try {
        myDate = dateFormat.parse(date_string)

    } catch (e: ParseException) {
        e.printStackTrace()
    }

    val timeFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    return timeFormat.format(myDate ?: Date())
}




