package com.cp.brittany.dixon.activities.home.models

data class ShippingAddressModel(
    val `data`: ShippingAddressData,
    val message: String,
    val status: String
)

data class AddShippingAddressModel(
    val `data`: ShippingAddresse,
    val message: String,
    val status: String
)

data class ShippingAddressData(
    val shipping_addresses: List<ShippingAddresse>
)

data class ShippingAddresse(
    val area: String = "",
    val city: String = "",
    val country: String = "",
    val created_at: String = "",
    val id: Int = 0,
    var is_default: Int = 0,
    val phone_number: String = "",
    val state: String = "",
    val street_address: String = "",
    val updated_at: String = "",
    val user_id: Int = 0,
    val zip_code: String = "",
    var is_selected: Boolean = false
)
