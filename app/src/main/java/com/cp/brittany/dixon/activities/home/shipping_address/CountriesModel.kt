package com.cp.brittany.dixon.activities.home.shipping_address

import android.os.Parcel
import android.os.Parcelable

data class CountriesModel(
    val `data`: Data,
    val message: String,
    val status: String
)

data class Data(
    val cities: List<CitiesData>,
    val countries: List<CountriesData>,
    val states: List<StatesData>
)

data class CitiesData(
    val id: Int,
    val name: String,
    val state_id: Int
)

data class CountriesData(
    val code: String,
    val id: Int,
    val name: String,
    val phonecode: Int
)

data class StatesData(
    val country_id: Int,
    val id: Int,
    val name: String
)