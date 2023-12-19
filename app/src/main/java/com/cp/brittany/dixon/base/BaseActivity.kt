package com.cp.brittany.dixon.base

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.ConfirmationDialog
import com.cp.brittany.dixon.activities.CustomLoginDialog
import com.cp.brittany.dixon.activities.OnAlertOneButtonClickListener
import com.cp.brittany.dixon.utils.HideUtil
import com.cp.brittany.dixon.utils.NetworkChangeReceiver
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import org.json.JSONObject
import java.text.*
import java.util.*


open class BaseActivity : AppCompatActivity(), NetworkChangeReceiver.ConnectivityReceiverListener {

    companion object {
        var isAppRunning: Boolean = false
    }

    lateinit var mViewGroup: ViewGroup
    var fontBold: Typeface? = null
    var fontMedium: Typeface? = null
    var fontRegular: Typeface? = null

    private var fbCallBackManager: CallbackManager? = null
    val activityLauncher: BaseActivityResult<Intent, ActivityResult> =
        BaseActivityResult.registerActivityForResult(this)
    private var mNetworkReceiver: BroadcastReceiver? = null

    open fun onSetupViewGroup(mVG: ViewGroup) {
        mViewGroup = mVG
        HideUtil.init(this, mViewGroup)
    }

    open fun getWidth(): Int {
        val screenWidth: Int
        val windowManager = windowManager
        val display = windowManager.defaultDisplay
        screenWidth = display.width
        return screenWidth
    }

    fun showLog(message: String) {
        Log.e("p4k", message)
    }

    @SuppressLint("ObjectAnimatorBinding")
    fun hoverEffect1(customView: Any): AnimatorSet {

        var animatorSet = AnimatorSet();
        var fadeOut: ObjectAnimator = ObjectAnimator.ofFloat(customView, "alpha", 1.0f, 0.1f)
        fadeOut.duration = 100
        var fadeIn: ObjectAnimator = ObjectAnimator.ofFloat(customView, "alpha", 0.1f, 1.0f)
        fadeIn.duration = 100
        animatorSet.play(fadeIn).after(fadeOut)
        return animatorSet
    }

    @SuppressLint("ObjectAnimatorBinding")
    fun hoverEffect2(customView: Any) {

        var animatorSet = AnimatorSet()
        var fadeOut: ObjectAnimator = ObjectAnimator.ofFloat(customView, "alpha", 1.0f, 0.1f)
        fadeOut.duration = 100
        var fadeIn: ObjectAnimator = ObjectAnimator.ofFloat(customView, "alpha", 0.1f, 1.0f)
        fadeIn.duration = 100
        animatorSet.play(fadeOut)
        animatorSet.start()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        initFonts()

        mNetworkReceiver = NetworkChangeReceiver(this)
        registerNetworkBroadcastForNougat()
        isAppRunning = true
    }

    public fun hideStatusBar_() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
    }

    fun checkPermissions(permissionArray: Array<String>): Boolean {
        for (i in permissionArray.indices) {
            if (ContextCompat.checkSelfPermission(
                    applicationContext, permissionArray[i]
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }


    fun googleSignIn(
        mGoogleSignInClient: GoogleSignInClient,
        function: (GoogleSignInAccount?) -> Unit
    ) {
        val signInIntent = mGoogleSignInClient.signInIntent
        activityLauncher.launch(
            signInIntent,
            object : BaseActivityResult.OnActivityResult<ActivityResult> {
                override fun onActivityResult(result: ActivityResult) {
                    Log.d("GoogleResult", result.toString())
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    handleSignInResult(task, function)
                }
            })
    }

    private fun handleSignInResult(
        completedTask: Task<GoogleSignInAccount>,
        function: (GoogleSignInAccount) -> Unit
    ) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.

            if (account != null) {
                function(account)
            }
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Google_SignIn", "signInResult:failed code=" + e.statusCode)
        }
    }

    fun facebookLogin(fbLoginButton: LoginButton, function: (JSONObject?) -> Unit) {
        fbCallBackManager = CallbackManager.Factory.create()

        fbLoginButton.setPermissions(listOf("email", "public_profile"))
        fbLoginButton.callOnClick()

        fbLoginButton.registerCallback(
            fbCallBackManager,
            object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    if (loginResult != null) {
                        val request = GraphRequest.newMeRequest(
                            loginResult.accessToken
                        ) { `object`, response ->
                            Log.v("LoginActivity", response.toString())
                            function(`object`)
                        }
                        val parameters = Bundle()
                        parameters.putString("fields", "id,name,email")
                        request.parameters = parameters
                        request.executeAsync()
                    }
                }

                override fun onCancel() {
                    Log.e("Facebook_Login", "Cancelled")
                }

                override fun onError(exception: FacebookException) {
                    Log.e("Facebook_Login", exception.toString())
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        fbCallBackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun signInWithApple(function: (AuthResult?) -> Unit) {
        //        FirebaseAuth.getInstance().signOut();  // To Logout user, apple account

        val auth = FirebaseAuth.getInstance()

        val scopes = ArrayList<String>()
        scopes.add("email")
        scopes.add("name")

        val provider = OAuthProvider.newBuilder("apple.com")
        provider.scopes = scopes
        // Localize the Apple authentication screen in French.
        provider.addCustomParameter("locale", "en")

        val pending = auth.pendingAuthResult
        if (pending != null) {
            pending.addOnSuccessListener { authResult ->
                Log.d("AppleLogin", "checkPending:onSuccess:$authResult")
                // Get the user profile with authResult.getUser() and
                // authResult.getAdditionalUserInfo(), and the ID
                // token from Apple with authResult.getCredential().

                function(authResult)
            }.addOnFailureListener { e ->
                Log.w("AppleLogin", "checkPending:onFailure", e)
            }
        } else {
            Log.d("AppleLogin", "pending: null")

            auth.startActivityForSignInWithProvider(this, provider.build())
                .addOnSuccessListener { authResult ->
                    // Sign-in successful!
                    Log.d("AppleLogin", "activitySignIn:onSuccess:${authResult.user}")
                    function(authResult)
                }
                .addOnFailureListener { e ->
                    Log.w("AppleLogin", "activitySignIn:onFailure", e)
                }
        }
    }

    fun changeFragmentWithoutReCreation(fragment: Fragment, tagFragmentName: String) {

        val mFragmentManager = supportFragmentManager
        val fragmentTransaction = mFragmentManager.beginTransaction()
        //fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        val currentFragment = mFragmentManager.primaryNavigationFragment
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment)
        }

        var fragmentTemp = mFragmentManager.findFragmentByTag(tagFragmentName)
        if (fragmentTemp == null) {
            fragmentTemp = fragment
            fragmentTransaction.add(R.id.fragment_container, fragmentTemp, tagFragmentName)
        } else {
            fragmentTransaction.show(fragmentTemp)
        }

        fragmentTransaction.setPrimaryNavigationFragment(fragmentTemp)
        fragmentTransaction.setReorderingAllowed(true)
        fragmentTransaction.commitNowAllowingStateLoss()
    }

    fun fullScreen() {
        window?.decorView?.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    private fun initFonts() {
        fontBold = ResourcesCompat.getFont(this, R.font.roboto_bold)
        fontMedium = ResourcesCompat.getFont(this, R.font.roboto_medium)
        fontRegular =
            ResourcesCompat.getFont(this, R.font.roboto_regular)
    }

    fun clearLightStatusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val window: Window = activity.window
            window.setStatusBarColor(
                ContextCompat
                    .getColor(activity, R.color.black)
            )
        }
    }


    fun blackStatusBarIcons() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    @SuppressLint("InlinedApi")
    fun changeStatusBarColor(resourceColor: Int) {

        val window: Window = window

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(applicationContext, resourceColor)

    }


    open fun hideKeyboard() {
        val imm: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view: View? = currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun <T> List<T>.toArrayList(): ArrayList<T> {
        return ArrayList(this)
    }

    fun isLocationEnabled(context: Context): Boolean {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun showGPSNotEnabledDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Enable Gps")
            .setMessage("Please enable the ")
            .setCancelable(false)
            .setPositiveButton("Enable Now!") { _, _ ->
                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .show()
    }

    fun isAccessFineLocationGranted(context: Context): Boolean {
        return ContextCompat
            .checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
    }

/*fun getMarkerIcon(color: String?): BitmapDescriptor? {
    val hsv = FloatArray(3)
    Color.colorToHSV(Color.parseColor(color), hsv)
    return BitmapDescriptorFactory.defaultMarker(hsv[0])
}*/


    private fun registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(
                mNetworkReceiver,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
        }
        registerReceiver(
            mNetworkReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    private fun unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    private lateinit var snackBar: Snackbar
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected) {
            if (this::snackBar.isInitialized)
                if (snackBar.isShown)
                    snackBar.dismiss()
        } else {
            snackBar =
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "No Internet Connection",
                    Snackbar.LENGTH_INDEFINITE
                )
            snackBar.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterNetworkChanges()
    }


    fun showDialog(title: String, data: String, isSingleButton: Boolean, listener: OnAlertOneButtonClickListener) {

        val dialog = ConfirmationDialog(title, data, isSingleButton, this, listener)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setContentView(R.layout.custom_confirmation_dialog)
//        val lp = WindowManager.LayoutParams()
//        lp.copyFrom(dialog.window?.attributes)
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
//        lp.gravity = Gravity.CENTER
//        dialog.window?.attributes = lp
        dialog.show(supportFragmentManager, "Custom Dialog")

    }

    fun showLoginDialog(isFromCart: Boolean = false) {

        val dialog = CustomLoginDialog(isFromCart, this)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setContentView(R.layout.login_dialog)
//        val lp = WindowManager.LayoutParams()
//        lp.copyFrom(dialog.window?.attributes)
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
//        lp.gravity = Gravity.CENTER
//        dialog.window?.attributes = lp
        dialog.show(supportFragmentManager, "Login Dialog")

    }
}

fun Activity.hideSystemUI() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.let {
            // Default behavior is that if navigation bar is hidden, the system will "steal" touches
            // and show it again upon user's touch. We just want the user to be able to show the
            // navigation bar by swipe, touches are handled by custom code -> change system bar behavior.
            // Alternative to deprecated SYSTEM_UI_FLAG_IMMERSIVE.
            it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            // make navigation bar translucent (alternative to deprecated
            // WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            // - do this already in hideSystemUI() so that the bar
            // is translucent if user swipes it up
            //window.navigationBarColor = getColor(R.color.internal_black_semitransparent_light)
            //window.navigationBarColor = getColor(R.color.transparent)
            // Finally, hide the system bars, alternative to View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            // and SYSTEM_UI_FLAG_FULLSCREEN.
            it.hide(WindowInsets.Type.statusBars())
        }
    } else {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
                // Do not let system steal touches for showing the navigation bar
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Hide the nav bar and status bar
//                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN)
        // Keep the app content behind the bars even if user swipes them up
//                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        //or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

        // make navbar translucent - do this already in hideSystemUI() so that the bar
        // is translucent if user swipes it up
        //@Suppress("DEPRECATION")
        //window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
    }
}

fun Activity.showSystemUI() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        // show app content in fullscreen, i. e. behind the bars when they are shown (alternative to
        // deprecated View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION and View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.setDecorFitsSystemWindows(false)
        // finally, show the system bars
        window.insetsController?.show(WindowInsets.Type.statusBars())
    } else {
        // Shows the system bars by removing all the flags
        // except for the ones that make the content appear under the system bars.
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
                //View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                //or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }
}
fun Activity.transparentStatusBar(){
    if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
        setWindowFlag(window, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
    }
    if (Build.VERSION.SDK_INT >= 19) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
    if (Build.VERSION.SDK_INT >= 21) {
        setWindowFlag(window, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR, false)
        window.statusBarColor = Color.TRANSPARENT
    }
}
private fun setWindowFlag(win: Window, bits: Int, on: Boolean) {

    val winParams = win.attributes
    if (on) {
        winParams.flags = winParams.flags or bits
    } else {
        winParams.flags = winParams.flags and bits.inv()
    }
    win.attributes = winParams
}

fun Activity.formatePickerDateNew2(date: Date): String {
    val formate = SimpleDateFormat("MMM dd,yyyy")
    return formate.format(date)

}
fun Activity.longDateFormat(date: Date): String {
    return DateFormat.getDateInstance(DateFormat.LONG, Locale.ENGLISH).format(date)
}
val Number.toPx get() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    Resources.getSystem().displayMetrics)

fun String.getAmount(): String {
    return substring(indexOfFirst { it.isDigit() }, indexOfLast { it.isDigit() } + 1)
        .filter { it.isDigit() || it == '.' }
}

fun String.getTime(): String{
    val f: NumberFormat = DecimalFormat("00")
    val min = (this.toInt() % (24 * 3600)) / 3600
    val hour = (this.toInt() % (24 * 3600 * 3600)) / 60
    val sec = (this.toInt() % (24 * 3600 * 3600 * 60))
    return if(sec < 60){
        "00:${f.format(sec)}"
    }
    else if(min>=1 && hour < 1.0){
        "${f.format(min)}:${f.format(sec)}"
    }
    else{
        "${f.format(hour)}:${f.format(min)}:${f.format(sec)}"
    }
}

fun getAbbreviatedFromDateTime(dateTime: String): String? {
    val input = SimpleDateFormat("yyyy-MM-dd")
    val output = SimpleDateFormat("dd MMM yyyy")
    return try {
        val getAbbreviate = input.parse(dateTime)    // parse input
        output.format(getAbbreviate!!)    // format output
    } catch (e: Exception) {
        e.printStackTrace()
        dateTime
    }
}