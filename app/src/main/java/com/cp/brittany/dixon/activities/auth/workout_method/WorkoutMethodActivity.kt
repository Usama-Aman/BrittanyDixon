package com.cp.brittany.dixon.activities.auth.workout_method

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModelProviders
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.auth.dietary_preference.DietaryPreferencesActivity
import com.cp.brittany.dixon.activities.auth.models.Preferences
import com.cp.brittany.dixon.activities.auth.models.PreferencesData
import com.cp.brittany.dixon.activities.auth.models.PreferencesModel
import com.cp.brittany.dixon.activities.view_models.AuthViewModel
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivityWorkoutMethodBinding
import com.cp.brittany.dixon.loader.Loader
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.ApiStatus
import com.cp.brittany.dixon.utils.AppUtils
import com.astritveliu.boom.Boom
import com.cp.brittany.dixon.activities.home.HomeActivity
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call

class WorkoutMethodActivity : BaseActivity() {
    private lateinit var binding: ActivityWorkoutMethodBinding
    private lateinit var adapter: WorkoutMethodsAdapter
    private lateinit var mContext: Context
    private lateinit var viewModel: AuthViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>
    private val preferenceList: MutableList<Preferences> = ArrayList()
    private var selectedWorkoutPref = ""
    private var isFromRegister = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkoutMethodBinding.inflate(layoutInflater)
        setContentView(binding.root)
        blackStatusBarIcons()
        changeStatusBarColor(R.color.white)

        mContext = this
        retrofitClient = RetrofitClient.getClient(this).create(Api::class.java)

        initVM()
        initAdapters()
        initViews()
        initListeners()
        observeApiResponse()

    }

    private fun initViews() {
        isFromRegister = intent?.getBooleanExtra("isFromRegister", false) ?: false
        if (isFromRegister) {
            binding.btnContinue.text = "Continue"
        }
        Boom(binding.btnContinue)
        Boom(binding.ivBack)
        getPreferences()
    }

    private fun initVM() {
        viewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        binding.lifecycleOwner = this
    }

    private fun observeApiResponse() {
        viewModel.getApiResponse().observe(this, {
            when (it.status) {
                ApiStatus.LOADING -> {
                    Loader.showLoader(this) {
                        if (this@WorkoutMethodActivity::apiCall.isInitialized)
                            apiCall.cancel()
                    }
                }
                ApiStatus.ERROR -> {
                    Loader.hideLoader()
                    AppUtils.showToast(this, it.message!!, false)
                }
                ApiStatus.SUCCESS -> {
                    Loader.hideLoader()
                    when (it.tag) {
                        ApiTags.GET_WORKOUT_PREFERENCES -> {
                            val model =
                                Gson().fromJson(it.data.toString(), PreferencesModel::class.java)
                            setWorkoutPreferences(model.data.prederences)
                        }
                        ApiTags.ASSIGN_PREFERENCE -> {
                            AppUtils.showToast(this, it.data?.getString("message")!!, true)
                            val userData = AppUtils.getUserDetails(this)
                            userData.workout_prefrence_id = selectedWorkoutPref
                            userData.total_workout_preferences = 1
                            AppUtils.saveUserModel(this, userData)

                            if (isFromRegister) {
                                Handler(Looper.getMainLooper()).postDelayed({
                                    val i = Intent(this, DietaryPreferencesActivity::class.java)
                                    i.putExtra("isFromRegister", isFromRegister)
                                    startActivity(i)
                                }, 1000)
                            }

                        }
                    }
                }
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setWorkoutPreferences(data: List<Preferences>) {
        preferenceList.addAll(data)
//        selectedWorkoutPrefList.forEach{ item->
//            preferenceList.forEach{
//                if(it.preference_data.id == item){
//                    it.preference_data.isSelected = true
//                }
//            }
//        }
        //if (selectedWorkoutPref != "")
//            if (preferenceList.any { it.preference_data.id == selectedWorkoutPref })
//                preferenceList.filter { it.preference_data.id == selectedWorkoutPref }[0].preference_data.isSelected = true
//        adapter.setItems(preferenceList)
        binding.rvWorkoutMethods.adapter?.notifyDataSetChanged()
    }

    private fun initListeners() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.btnContinue.setOnClickListener {
            //if (selectedWorkoutPref != "") {
            selectedWorkoutPref = ""
            preferenceList.forEach {
                if (it.preference_data.is_selected == 1) {
                    selectedWorkoutPref = if (selectedWorkoutPref == "")
                        "${selectedWorkoutPref}${it.preference_data.id}"
                    else
                        "${selectedWorkoutPref},${it.preference_data.id}"
                }
            }
            if (selectedWorkoutPref != "")
                assignPreference()
            else
                AppUtils.showToast(this, "Please select any Preference first!", false)

//            } else {
//                AppUtils.showToast(this, "Please select any Preference first!", false)
//            }
        }
    }

    private fun initAdapters() {
        adapter = WorkoutMethodsAdapter(this, preferenceList, object : WorkoutPreferencesListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onItemClicked(position: Int, model: PreferencesData) {
                selectedWorkoutPref = model.id.toString()

                //if (!model.isSelected) {
//                    preferenceList.forEachIndexed { _, Preferences ->
//                        Preferences.preference_data.isSelected = false
//                    }
                if (preferenceList[position].preference_data.is_selected == 1)
                    preferenceList[position].preference_data.is_selected = 0
                else
                    preferenceList[position].preference_data.is_selected = 1
                adapter.notifyDataSetChanged()
                //}
            }

            override fun onItemSeeMoreClicked(position: Int) {
                val dialog = WorkoutMethodDetailPopup(preferenceList[position].profiles_data, preferenceList[position].preference_data)
                dialog.show(supportFragmentManager, "WorkoutMethodDetailPopup")
            }
        })
        binding.rvWorkoutMethods.adapter = adapter
    }

    private fun getPreferences() {
        apiCall = retrofitClient.getWorkoutPreferences()
        viewModel.getWorkoutPreferences(apiCall)
    }

    private fun assignPreference() {
        apiCall = retrofitClient.assignWorkoutPreference(selectedWorkoutPref, "workout")
        viewModel.assignPreference(apiCall)
    }

}