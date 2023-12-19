package com.cp.brittany.dixon.activities


import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.auth.dietary_preference.DietaryPreferencesActivity
import com.cp.brittany.dixon.activities.auth.verify_otp.VerifyOtpActivity
import com.cp.brittany.dixon.activities.auth.workout_method.WorkoutMethodActivity
import com.cp.brittany.dixon.activities.boarding.BoardingActivity
import com.cp.brittany.dixon.activities.home.HomeActivity
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivitySplashBinding
import com.cp.brittany.dixon.notification.NotificationModel
import com.cp.brittany.dixon.utils.AppUtils
import com.cp.brittany.dixon.utils.Constants
import com.cp.brittany.dixon.utils.SharedPreference
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding
    private var googleAccount: GoogleSignInAccount? = null
    private var facebookAccessToken: AccessToken? = null
    private var referralCode = ""
    private var workoutCode = ""
    private var insightCode = ""
    private lateinit var model: NotificationModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor(R.color.grey_900)
        fullScreen()

        initVar()
        printHashKey(this)

        googleAccount = GoogleSignIn.getLastSignedInAccount(this)
        facebookAccessToken = AccessToken.getCurrentAccessToken()

        Handler(Looper.getMainLooper()).postDelayed({
            getLinkFromIntent(intent)
        }, 1000)
    }

    private fun initVar() {
    }

    private fun getLinkFromIntent(intent: Intent?) {
        val data = intent?.data

        if (intent != null) {
            if(intent.hasExtra("myData"))
                model = intent.getParcelableExtra("myData")!!
        }
        if (data != null) {
            if (intent.hasExtra("myData"))
                model = intent.getParcelableExtra("myData")!!
            //val parameters = data.pathSegments
            referralCode = data.getQueryParameter(Constants.referralCode) ?: ""
            workoutCode = data.getQueryParameter(Constants.workoutCode) ?: ""
            insightCode = data.getQueryParameter(Constants.insightCode) ?: ""

        }
        /*  AppController.profileReferralCode = data?.getQueryParameter(Constants.referralCode).toString()*/
        checkForLoggedIn()
    }

    private fun checkForLoggedIn() {

        if (SharedPreference.getBoolean(this@SplashActivity, Constants.isUserLoggedIn)) {
            if (googleAccount != null)
                if (!googleAccount!!.isExpired) {
                    if (validate()) {
                        val i = Intent(this@SplashActivity, HomeActivity::class.java)
                        when {
                            ::model.isInitialized -> {
                                i.putExtra("myData", model)
                            }
                            referralCode != "" -> {
                                i.putExtra(Constants.referralCode, referralCode)
                            }
                            workoutCode != "" -> {
                                i.putExtra(Constants.workoutCode, workoutCode)
                            }
                            insightCode != "" -> {
                                i.putExtra(Constants.insightCode, insightCode)
                            }
                        }
                        startActivity(i)
                        finish()
                    }
                }
            if (facebookAccessToken != null) {
                if (!facebookAccessToken!!.isExpired) {
                    if (validate()) {
                        val i = Intent(this@SplashActivity, HomeActivity::class.java)
                        when {
                            ::model.isInitialized -> {
                                i.putExtra("myData", model)
                            }
                            referralCode != "" -> {
                                i.putExtra(Constants.referralCode, referralCode)
                            }
                            workoutCode != "" -> {
                                i.putExtra(Constants.workoutCode, workoutCode)
                            }
                            insightCode != "" -> {
                                i.putExtra(Constants.insightCode, insightCode)
                            }
                        }
                        startActivity(i)
                        finish()
                    }
                }
            } else {
                if (validate()) {
                    val i = Intent(this@SplashActivity, HomeActivity::class.java)
                    when {
                        ::model.isInitialized -> {
                            i.putExtra("myData", model)
                        }
                        referralCode != "" -> {
                            i.putExtra(Constants.referralCode, referralCode)
                        }
                        workoutCode != "" -> {
                            i.putExtra(Constants.workoutCode, workoutCode)
                        }
                        insightCode != "" -> {
                            i.putExtra(Constants.insightCode, insightCode)
                        }
                    }
                    startActivity(i)
                    finish()
                }
            }
        } else {
            startActivity(Intent(this@SplashActivity, BoardingActivity::class.java))
            finish()
        }
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        getLinkFromIntent(intent)
    }

    private fun validate(): Boolean {
        val data = AppUtils.getUserDetails(this)

        when {
            data.is_guest == 1 -> {
                return true
            }
            data.email_verified_at == 0 -> {
                intent = Intent(this, VerifyOtpActivity::class.java)
                intent.putExtra("type", "email")
                intent.putExtra("email", data.email)
                intent.putExtra("fromProfile", false)
                startActivity(intent)
                return false
            }
            data.total_workout_preferences == 0 -> {
                intent = Intent(this, WorkoutMethodActivity::class.java)
                intent.putExtra("isFromRegister", true)
                startActivity(intent)
                return false
            }
            data.total_diet_preferences == 0 -> {
                intent = Intent(this, DietaryPreferencesActivity::class.java)
                intent.putExtra("isFromRegister", true)
                startActivity(intent)
                return false
            }
            else -> return true
        }
    }

    private fun printHashKey(pContext: Context) {
        try {
            val info: PackageInfo = pContext.packageManager
                .getPackageInfo(pContext.packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey: String = String(Base64.encode(md.digest(), 0))
                Log.i("TAG", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("TAG", "printHashKey()", e)
        } catch (e: Exception) {
            Log.e("TAG", "printHashKey()", e)
        }
    }
}