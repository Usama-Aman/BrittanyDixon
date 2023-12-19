package com.cp.brittany.dixon.activities.auth.dietary_preference

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModelProviders
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.auth.models.Preferences
import com.cp.brittany.dixon.activities.auth.models.PreferencesData
import com.cp.brittany.dixon.activities.auth.models.PreferencesModel
import com.cp.brittany.dixon.activities.auth.workout_method.WorkoutMethodDetailPopup
import com.cp.brittany.dixon.activities.auth.workout_method.WorkoutPreferencesListener
import com.cp.brittany.dixon.activities.home.HomeActivity
import com.cp.brittany.dixon.activities.view_models.AuthViewModel
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivityDietaryPreferencesBinding
import com.cp.brittany.dixon.loader.Loader
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.ApiStatus
import com.cp.brittany.dixon.utils.AppUtils
import com.astritveliu.boom.Boom
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call

class DietaryPreferencesActivity : BaseActivity() {
    private lateinit var binding: ActivityDietaryPreferencesBinding
    private lateinit var adapter: DietaryPreferencesAdapter
    private lateinit var mContext: Context
    private lateinit var viewModel: AuthViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>
    private val preferenceList: MutableList<Preferences> = ArrayList()
    private var selectedDietaryPref = ""
    private var isFromRegister = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDietaryPreferencesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()

        mContext = this
        retrofitClient = RetrofitClient.getClient(this).create(Api::class.java)

        initVM()
        observeApiResponse()
        initViews()
        initListeners()
        initAdapters()
    }

    private fun initViews() {
        isFromRegister = intent?.getBooleanExtra("isFromRegister", false) ?: false
        Boom(binding.ivBack)
        Boom(binding.btnFinish)
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
                        if (this@DietaryPreferencesActivity::apiCall.isInitialized)
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
                        ApiTags.GET_DIETARY_PREFERENCES -> {
                            val model =
                                Gson().fromJson(it.data.toString(), PreferencesModel::class.java)
                            setDietaryPreferences(model.data.prederences)
                        }
                        ApiTags.ASSIGN_PREFERENCE -> {
                            AppUtils.showToast(this, it.data?.getString("message")!!, true)
                            val userData = AppUtils.getUserDetails(this)
                            userData.diet_prefrence_id = selectedDietaryPref
                            userData.total_diet_preferences = 1
                            AppUtils.saveUserModel(this, userData)

                            if(isFromRegister) {
                                Handler(Looper.getMainLooper()).postDelayed({
                                    val intent = Intent(this, HomeActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    startActivity(intent)
                                    finish()
                                }, 1000)
                            }

                        }
                    }
                }
            }
        })
    }

    private fun setDietaryPreferences(list: List<Preferences>) {
        preferenceList.addAll(list)
//        if (selectedDietaryPref != "")
//            if (preferenceList.any { it.preference_data.id == selectedDietaryPref })
//                preferenceList.filter { it.preference_data.id == selectedDietaryPref }[0].preference_data.isSelected = true
//        selectedDietaryPrefList.forEach{ item->
//            preferenceList.forEach{
//                if(it.preference_data.id == item){
//                    it.preference_data.isSelected = true
//                }
//            }
//        }
        adapter.setItems(preferenceList)
    }

    private fun initListeners() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btnFinish.setOnClickListener {
//            if (selectedDietaryPref != "") {
//                selectedDietaryPref = ""
            selectedDietaryPref = ""
            preferenceList.forEach {
                if (it.preference_data.is_selected == 1) {
                    selectedDietaryPref = if (selectedDietaryPref == "")
                        "${selectedDietaryPref}${it.preference_data.id}"
                    else
                        "${selectedDietaryPref},${it.preference_data.id}"
                }
            }
            if (selectedDietaryPref != "")
                assignPreference()
            else
                AppUtils.showToast(this, "Please select any Preference first!", false)
//            } else {
//                AppUtils.showToast(this, "Please select any Preference first!", false)
//            }
        }
    }

    private fun initAdapters() {
        adapter = DietaryPreferencesAdapter(this, preferenceList, object :
            WorkoutPreferencesListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onItemClicked(position: Int, model: PreferencesData) {
                //selectedDietaryPref = model.id.toString()

                //if (!model.isSelected) {
//                    preferenceList.forEachIndexed { _, Preferences ->
//                        Preferences.preference_data.isSelected = false
//                    }
                if(preferenceList[position].preference_data.is_selected == 1)
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
        binding.rvDietaryPreferences.adapter = adapter
    }

    private fun getPreferences() {
        apiCall = retrofitClient.getDietaryPreferences()
        viewModel.getDietaryPreferences(apiCall)
    }

    private fun assignPreference() {
        apiCall = retrofitClient.assignDietPreference(selectedDietaryPref, "diet")
        viewModel.assignPreference(apiCall)
    }
}