package com.cp.brittany.dixon.activities.home.search.insight_tab

import android.os.Parcel
import android.os.Parcelable
import com.cp.brittany.dixon.activities.home.search.food_tab.FilterCategory

data class InsightFilterModel(
    val `data`: InsightFilterData,
    val message: String,
    val status: String
)

data class InsightFilterData(
    val categories: List<FilterCategory>,
    val max_duration: String = "1"
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.createTypedArrayList(FilterCategory)!!,
        parcel.readString() ?: ""
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(categories)
        parcel.writeString(max_duration)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<InsightFilterData> {
        override fun createFromParcel(parcel: Parcel): InsightFilterData {
            return InsightFilterData(parcel)
        }

        override fun newArray(size: Int): Array<InsightFilterData?> {
            return arrayOfNulls(size)
        }
    }
}

data class InsightFilterResult(
    var categoryName: String = "",
    var categoryId: Int = -1,
    var duration_value: Float = 1f,
    var duration_interval: Int = 0,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readFloat(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(categoryName)
        parcel.writeInt(categoryId)
        parcel.writeFloat(duration_value)
        parcel.writeInt(duration_interval)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<InsightFilterResult> {
        override fun createFromParcel(parcel: Parcel): InsightFilterResult {
            return InsightFilterResult(parcel)
        }

        override fun newArray(size: Int): Array<InsightFilterResult?> {
            return arrayOfNulls(size)
        }
    }
}
