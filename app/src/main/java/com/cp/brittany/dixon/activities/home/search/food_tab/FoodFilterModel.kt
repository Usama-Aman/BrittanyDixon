package com.cp.brittany.dixon.activities.home.search.food_tab

import android.os.Parcel
import android.os.Parcelable

data class FoodFilterModel(
    val `data`: FoodFilterData,
    val message: String,
    val status: String
)

data class FoodFilterData(
    val categories: List<FilterCategory>,
    val ingredients: List<FoodFilterIngredient>,
    val max_calories: String,
    val max_time: String,
    val type: List<String>,
    val weight_status: List<String>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.createTypedArrayList(FilterCategory)!!,
        parcel.createTypedArrayList(FoodFilterIngredient)!!,
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList()!!,
        parcel.createStringArrayList()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(categories)
        parcel.writeTypedList(ingredients)
        parcel.writeString(max_calories)
        parcel.writeString(max_time)
        parcel.writeStringList(type)
        parcel.writeStringList(weight_status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FoodFilterData> {
        override fun createFromParcel(parcel: Parcel): FoodFilterData {
            return FoodFilterData(parcel)
        }

        override fun newArray(size: Int): Array<FoodFilterData?> {
            return arrayOfNulls(size)
        }
    }
}

data class FilterCategory(
    val description: String,
    val id: Int,
    val name: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(description)
        parcel.writeInt(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FilterCategory> {
        override fun createFromParcel(parcel: Parcel): FilterCategory {
            return FilterCategory(parcel)
        }

        override fun newArray(size: Int): Array<FilterCategory?> {
            return arrayOfNulls(size)
        }
    }
}

data class FoodFilterIngredient(
    val id: Int = 0,
    val name: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: ""
    ) {
    }


    data class FoodFilterType(
        val `0`: String
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
    }


    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FoodFilterIngredient> {
        override fun createFromParcel(parcel: Parcel): FoodFilterIngredient {
            return FoodFilterIngredient(parcel)
        }

        override fun newArray(size: Int): Array<FoodFilterIngredient?> {
            return arrayOfNulls(size)
        }
    }
}

data class FoodFilterResult(
    var categoryId: Int = 0,
    var foodType: String = "",
    var weight: String = "",
    var calories: Float = 1f,
    var caloriesIntervalNo: Int = 0,
    var ingredientIds: List<FoodFilterResultIngredient> = ArrayList(),
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readFloat(),
        parcel.readInt(),
        parcel.createTypedArrayList(FoodFilterResultIngredient) ?: ArrayList<FoodFilterResultIngredient>()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(categoryId)
        parcel.writeString(foodType)
        parcel.writeString(weight)
        parcel.writeFloat(calories)
        parcel.writeInt(caloriesIntervalNo)
        parcel.writeTypedList(ingredientIds)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FoodFilterResult> {
        override fun createFromParcel(parcel: Parcel): FoodFilterResult {
            return FoodFilterResult(parcel)
        }

        override fun newArray(size: Int): Array<FoodFilterResult?> {
            return arrayOfNulls(size)
        }
    }
}

data class FoodFilterResultIngredient(
    var id: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FoodFilterResultIngredient> {
        override fun createFromParcel(parcel: Parcel): FoodFilterResultIngredient {
            return FoodFilterResultIngredient(parcel)
        }

        override fun newArray(size: Int): Array<FoodFilterResultIngredient?> {
            return arrayOfNulls(size)
        }
    }
}