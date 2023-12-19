package com.cp.brittany.dixon.activities.home.models

import android.os.Parcel
import android.os.Parcelable

data class ProfileModel(
    val `data`: ProfileData,
    val message: String,
    val status: String
)

data class ProfileData(
    val bookmarks: Int,
    val card: Card,
    val cart_items: Int,
    val shipping_addresses: List<ShippingAddress>,
    val user_data: UserData
)

data class Card(
    val card_type: String,
    val created_at: String,
    val exp_month: Int,
    val exp_year: Int,
    val id: Int,
    val is_default: Int,
    val last4: String,
    val stripe_card_id: String,
    val updated_at: String,
    val user_id: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(card_type)
        parcel.writeString(created_at)
        parcel.writeInt(exp_month)
        parcel.writeInt(exp_year)
        parcel.writeInt(id)
        parcel.writeInt(is_default)
        parcel.writeString(last4)
        parcel.writeString(stripe_card_id)
        parcel.writeString(updated_at)
        parcel.writeInt(user_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Card> {
        override fun createFromParcel(parcel: Parcel): Card {
            return Card(parcel)
        }

        override fun newArray(size: Int): Array<Card?> {
            return arrayOfNulls(size)
        }
    }

}

data class ShippingAddress(
    val area: String,
    val city: String,
    val country: String,
    val created_at: String,
    val id: Int,
    val is_default: Int,
    val phone_number: String,
    val state: String,
    val street_address: String,
    val updated_at: String,
    val user_id: Int,
    val zip_code: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(area)
        parcel.writeString(city)
        parcel.writeString(country)
        parcel.writeString(created_at)
        parcel.writeInt(id)
        parcel.writeInt(is_default)
        parcel.writeString(phone_number)
        parcel.writeString(state)
        parcel.writeString(street_address)
        parcel.writeString(updated_at)
        parcel.writeInt(user_id)
        parcel.writeString(zip_code)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ShippingAddress> {
        override fun createFromParcel(parcel: Parcel): ShippingAddress {
            return ShippingAddress(parcel)
        }

        override fun newArray(size: Int): Array<ShippingAddress?> {
            return arrayOfNulls(size)
        }
    }

}

data class UserData(
    val address: String,
    val city: String,
    val country: String,
    val created_at: String,
    val date_of_birth: String,
    val diet_prefrence_id: Int,
    val email: String,
    val email_verified_at: String,
    val gender: String,
    val height: Height,
    val id: Int,
    var image: String,
    val image_path: String,
    val is_blocked: Int,
    val is_email_verified: Int,
    val name: String,
    val provider: String,
    val provider_id: Int,
    val social_id: String,
    val social_type: String,
    val state: String,
    val stripe_user_id: String,
    val updated_at: String,
    val verification_code: String,
    val weight: Weight,
    val workout_prefrence_id: Int,
    val zip_code: String,
    val total_workout_preferences: Int = 0,
    val total_diet_preferences: Int = 0,
    val user_weight_unit: String = "",
    val user_height_unit: String = "",
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readParcelable(Height::class.java.classLoader)!!,
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readParcelable(Weight::class.java.classLoader)!!,
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(address)
        parcel.writeString(city)
        parcel.writeString(country)
        parcel.writeString(created_at)
        parcel.writeString(date_of_birth)
        parcel.writeInt(diet_prefrence_id)
        parcel.writeString(email)
        parcel.writeString(email_verified_at)
        parcel.writeString(gender)
        parcel.writeParcelable(height, flags)
        parcel.writeInt(id)
        parcel.writeString(image)
        parcel.writeString(image_path)
        parcel.writeInt(is_blocked)
        parcel.writeInt(is_email_verified)
        parcel.writeString(name)
        parcel.writeString(provider)
        parcel.writeInt(provider_id)
        parcel.writeString(social_id)
        parcel.writeString(social_type)
        parcel.writeString(state)
        parcel.writeString(stripe_user_id)
        parcel.writeString(updated_at)
        parcel.writeString(verification_code)
        parcel.writeParcelable(weight, flags)
        parcel.writeInt(workout_prefrence_id)
        parcel.writeString(zip_code)
        parcel.writeInt(total_workout_preferences)
        parcel.writeInt(total_diet_preferences)
        parcel.writeString(user_weight_unit)
        parcel.writeString(user_height_unit)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserData> {
        override fun createFromParcel(parcel: Parcel): UserData {
            return UserData(parcel)
        }

        override fun newArray(size: Int): Array<UserData?> {
            return arrayOfNulls(size)
        }
    }
}

data class Height(
    val height: String,
    val height_unit: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(height)
        parcel.writeString(height_unit)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Height> {
        override fun createFromParcel(parcel: Parcel): Height {
            return Height(parcel)
        }

        override fun newArray(size: Int): Array<Height?> {
            return arrayOfNulls(size)
        }
    }
}

data class Weight(
    val weight: String,
    val weight_unit: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(weight)
        parcel.writeString(weight_unit)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Weight> {
        override fun createFromParcel(parcel: Parcel): Weight {
            return Weight(parcel)
        }

        override fun newArray(size: Int): Array<Weight?> {
            return arrayOfNulls(size)
        }
    }
}