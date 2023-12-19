package com.cp.brittany.dixon.activities.home.models

import android.os.Parcel
import android.os.Parcelable

data class InsightRecommendationDetailModel(
    val `data`: InsightRecommendationDetailData,
    val message: String,
    val status: String
)

data class InsightRecommendationDetailData(
    val created_at: String,
    val date: String,
    val detail: String,
    val duration: String,
    val id: Int,
    val image: String,
    val image_path: String,
    val insight_category: InsightCategory,
    val insight_category_id: Int,
    val is_bookmarked: Int,
    var is_liked: Boolean,
    val name: String,
    var total_likes: Int,
    val unit: String,
    val updated_at: String
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readInt(),
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readParcelable(InsightCategory::class.java.classLoader)!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readString()?:"",
        parcel.readInt(),
        parcel.readString()?:"",
        parcel.readString()?:""
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(created_at)
        parcel.writeString(date)
        parcel.writeString(detail)
        parcel.writeString(duration)
        parcel.writeInt(id)
        parcel.writeString(image)
        parcel.writeString(image_path)
        parcel.writeParcelable(insight_category, flags)
        parcel.writeInt(insight_category_id)
        parcel.writeInt(is_bookmarked)
        parcel.writeByte(if (is_liked) 1 else 0)
        parcel.writeString(name)
        parcel.writeInt(total_likes)
        parcel.writeString(unit)
        parcel.writeString(updated_at)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<InsightRecommendationDetailData> {
        override fun createFromParcel(parcel: Parcel): InsightRecommendationDetailData {
            return InsightRecommendationDetailData(parcel)
        }

        override fun newArray(size: Int): Array<InsightRecommendationDetailData?> {
            return arrayOfNulls(size)
        }
    }
}

data class InsightCategory(
    val created_at: String,
    val description: String,
    val diet_prefrence_id: Int,
    val gender: String,
    val id: Int,
    val name: String,
    val updated_at: String,
    val workout_prefrence_id: Int
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readInt(),
        parcel.readString()?:"",
        parcel.readInt(),
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(created_at)
        parcel.writeString(description)
        parcel.writeInt(diet_prefrence_id)
        parcel.writeString(gender)
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(updated_at)
        parcel.writeInt(workout_prefrence_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<InsightCategory> {
        override fun createFromParcel(parcel: Parcel): InsightCategory {
            return InsightCategory(parcel)
        }

        override fun newArray(size: Int): Array<InsightCategory?> {
            return arrayOfNulls(size)
        }
    }
}