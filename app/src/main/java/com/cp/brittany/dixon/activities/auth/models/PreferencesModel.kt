package com.cp.brittany.dixon.activities.auth.models

data class PreferencesModel(
    val `data`: Data,
    val message: String,
    val status: String
)

data class Data(
    val prederences: List<Preferences>,
    val total_preferences: Int
)

data class Preferences(
    val preference_data: PreferencesData,
    val profiles_data: List<ProfilesData>
)

data class PreferencesData(
    val description: String,
    val id: Int? = -1,
    val image: String,
    val image_path: String,
    val name: String,
    var is_selected: Int = 0
)

data class ProfilesData(
    val name: String,
    val profiles_data: List<ProfilesDataList>
)

data class ProfilesDataList(
    val name: String,
    val type: String
)