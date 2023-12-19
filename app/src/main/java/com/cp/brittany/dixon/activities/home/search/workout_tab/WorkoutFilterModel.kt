package com.cp.brittany.dixon.activities.home.search.workout_tab

import android.os.Parcel
import android.os.Parcelable
import com.cp.brittany.dixon.activities.home.search.food_tab.FilterCategory

data class WorkoutFilterModel(
    val `data`: WorkoutFilterData,
    val message: String,
    val status: String
)

data class WorkoutFilterData(
    val categories: List<FilterCategory>,
    val max_duration: WorkoutFilterMaxDuration
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.createTypedArrayList(FilterCategory)!!,
        parcel.readParcelable(WorkoutFilterMaxDuration::class.java.classLoader)!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(categories)
        parcel.writeParcelable(max_duration, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WorkoutFilterData> {
        override fun createFromParcel(parcel: Parcel): WorkoutFilterData {
            return WorkoutFilterData(parcel)
        }

        override fun newArray(size: Int): Array<WorkoutFilterData?> {
            return arrayOfNulls(size)
        }
    }
}

data class WorkoutFilterMaxDuration(
    val max_duration: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(max_duration)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WorkoutFilterMaxDuration> {
        override fun createFromParcel(parcel: Parcel): WorkoutFilterMaxDuration {
            return WorkoutFilterMaxDuration(parcel)
        }

        override fun newArray(size: Int): Array<WorkoutFilterMaxDuration?> {
            return arrayOfNulls(size)
        }
    }
}

data class WorkoutFilterResult(
    var categoryName: String = "",
    var gender: String = "Male",
    var searchWord: String = "",
    var difficulty: Int = 0,
    var difficulty_value: Float = 1f,
    var categoryId: Int = -1,
    var durationIntervalNo: Int = 0,
    var duration_value: Float = 1f,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readFloat(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readFloat()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(categoryName)
        parcel.writeString(gender)
        parcel.writeString(searchWord)
        parcel.writeInt(difficulty)
        parcel.writeFloat(difficulty_value)
        parcel.writeInt(categoryId)
        parcel.writeInt(durationIntervalNo)
        parcel.writeFloat(duration_value)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WorkoutFilterResult> {
        override fun createFromParcel(parcel: Parcel): WorkoutFilterResult {
            return WorkoutFilterResult(parcel)
        }

        override fun newArray(size: Int): Array<WorkoutFilterResult?> {
            return arrayOfNulls(size)
        }
    }
}