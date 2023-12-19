package com.cp.brittany.dixon.activities.home.home_models

import android.os.Parcel
import android.os.Parcelable

data class AllFoodsModel(
    val `data`: AllFoodsData,
    val message: String,
    val status: String
)

data class FoodsModel(
    val `data`: List<FoodsList>,
    val message: String,
    val status: String
)

data class AllFoodsData(
    val foods: List<FoodsList>,
    val images_baseUrl: AllFoodsImagesBaseUrl
)

data class FoodsList(
    val calories: String,
    val created_at: String,
    val diet_prefrence_id: Int,
    val food_category_id: Int,
    val food_status: String,
    val id: Int,
    val image: String,
    val image_path: String,
    val name: String,
    val recipe: String,
    val time: String,
    val type: String,
    val unit: String,
    val updated_at: String,
    val weight_status: String,
    var is_bookmarked: Int,
    val video: String,
    val video_path: String,
    var isAddedToUserFoods: Boolean = false,

)
//    : Parcelable{
//    constructor(parcel: Parcel) : this(
//        parcel.readString()?: "",
//        parcel.readString()?: "",
//        parcel.readInt(),
//        parcel.readInt(),
//        parcel.readString()?: "",
//        parcel.readInt(),
//        parcel.readString()?: "",
//        parcel.readString()?: "",
//        parcel.readString()?: "",
//        parcel.readString()?: "",
//        parcel.readString()?: "",
//        parcel.readString()?: "",
//        parcel.readString()?: "",
//        parcel.readString()?: "",
//        parcel.readString()?: "",
//        parcel.readInt(),
//        parcel.readString()?: "",
//    ) {
//    }
//
//    override fun writeToParcel(parcel: Parcel, flags: Int) {
//        parcel.writeString(calories)
//        parcel.writeString(created_at)
//        parcel.writeInt(diet_prefrence_id)
//        parcel.writeInt(food_category_id)
//        parcel.writeString(food_status)
//        parcel.writeInt(id)
//        parcel.writeString(image)
//        parcel.writeString(image_path)
//        parcel.writeString(name)
//        parcel.writeString(recipe)
//        parcel.writeString(time)
//        parcel.writeString(type)
//        parcel.writeString(unit)
//        parcel.writeString(updated_at)
//        parcel.writeString(weight_status)
//        parcel.writeInt(is_bookmarked)
//        parcel.writeString(video)
//    }
//
//    override fun describeContents(): Int {
//        return 0
//    }
//
//    companion object CREATOR : Parcelable.Creator<FoodsList> {
//        override fun createFromParcel(parcel: Parcel): FoodsList {
//            return FoodsList(parcel)
//        }
//
//        override fun newArray(size: Int): Array<FoodsList?> {
//            return arrayOfNulls(size)
//        }
//    }
//
//
//}

data class AllFoodsImagesBaseUrl(
    val foods: String
)