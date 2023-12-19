package com.cp.brittany.dixon.activities.home.workout_detail

import android.os.Parcel
import android.os.Parcelable

data class WorkoutDetailModel(
    val `data`: WorkoutDetailData,
    val message: String,
    val status: String
)

data class WorkoutDetailData(
    val workouts: Workouts
)

data class Workouts(
    val categories: Categories,
    val category_id: Int,
    val description: String,
    val id: Int,
    val image: String,
    val image_path: String,
    val is_bookmarked: Int,
    val is_free: Int,
    val level: Int,
    val name: String,
    val percentage_completed: Double,
    val weight_status: String,
    val workout_prefrence_id: Int,
    val workout_sections: List<WorkoutSection>,
    val duration: String,
    val cal_gain_reduce: String
)

data class Categories(
    val description: String,
    val id: Int,
    val image: Any,
    val image_path: String,
    val name: String,
    val no_equipment: Int
)

data class WorkoutSection(
    val created_at: String? = "",
    var get_this_workout_lessons: ArrayList<GetThisWorkoutLesson> = ArrayList(),
    val id: Int = 0,
    val lesson_id: Int = 0,
    val name: String = "",
    val parent_set_id: String = "",
    val percentage_completed: Double = 0.0,
    val section_name: String? = "",
    val sort_order: String = "",
    val updated_at: String = "",
    val workout_id: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.createTypedArrayList(GetThisWorkoutLesson) ?: ArrayList(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(created_at)
        parcel.writeTypedList(get_this_workout_lessons)
        parcel.writeInt(id)
        parcel.writeInt(lesson_id)
        parcel.writeString(name)
        parcel.writeString(parent_set_id)
        parcel.writeDouble(percentage_completed)
        parcel.writeString(section_name)
        parcel.writeString(sort_order)
        parcel.writeString(updated_at)
        parcel.writeInt(workout_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WorkoutSection> {
        override fun createFromParcel(parcel: Parcel): WorkoutSection {
            return WorkoutSection(parcel)
        }

        override fun newArray(size: Int): Array<WorkoutSection?> {
            return arrayOfNulls(size)
        }
    }
}

data class GetThisWorkoutLesson(
    val childs: ArrayList<Child> = ArrayList(),
    val created_at: String = "",
    val duration: String? = "",
    val duration_unit: String? = "",
    val file_id: Int = 0,
    var files: Files? = null,
    val id: Int = 0,
    val is_rest: Int = 0,
    val name: String? = "",
    val no_of_set: String = "",
    val parent_id: Int = 0,
    val reps: String? = "",
    val sort_order: String? = "",
    val thumbnail: String? = "",
    val type: String? = "",
    val updated_at: String? = "",
    val video: String? = "",
    val watch_time: WatchTime?,
    val workout_section_id: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.createTypedArrayList(Child) ?: ArrayList(),
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readParcelable(Files::class.java.classLoader),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(WatchTime::class.java.classLoader),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(childs)
        parcel.writeString(created_at)
        parcel.writeString(duration)
        parcel.writeString(duration_unit)
        parcel.writeInt(file_id)
        parcel.writeParcelable(files, flags)
        parcel.writeInt(id)
        parcel.writeInt(is_rest)
        parcel.writeString(name)
        parcel.writeString(no_of_set)
        parcel.writeInt(parent_id)
        parcel.writeString(reps)
        parcel.writeString(sort_order)
        parcel.writeString(thumbnail)
        parcel.writeString(type)
        parcel.writeString(updated_at)
        parcel.writeString(video)
        parcel.writeParcelable(watch_time, flags)
        parcel.writeInt(workout_section_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GetThisWorkoutLesson> {
        override fun createFromParcel(parcel: Parcel): GetThisWorkoutLesson {
            return GetThisWorkoutLesson(parcel)
        }

        override fun newArray(size: Int): Array<GetThisWorkoutLesson?> {
            return arrayOfNulls(size)
        }
    }
}

data class Child(
    val created_at: String = "",
    val duration: String = "",
    val duration_unit: String = "",
    val file_id: Int = 0,
    var files: Files? = null,
    val id: Int = 0,
    val is_rest: Int = 0,
    val name: String = "",
    val no_of_set: String = "",
    val parent_id: String = "",
    val reps: String = "",
    val sort_order: String = "",
    val thumbnail: String = "",
    val type: String = "",
    val updated_at: String = "",
    val video: String = "",
    val watch_time: WatchTime?,
    val workout_section_id: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readParcelable(Files::class.java.classLoader),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readParcelable(WatchTime::class.java.classLoader),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(created_at)
        parcel.writeString(duration)
        parcel.writeString(duration_unit)
        parcel.writeInt(file_id)
        parcel.writeParcelable(files, flags)
        parcel.writeInt(id)
        parcel.writeInt(is_rest)
        parcel.writeString(name)
        parcel.writeString(no_of_set)
        parcel.writeString(parent_id)
        parcel.writeString(reps)
        parcel.writeString(sort_order)
        parcel.writeString(thumbnail)
        parcel.writeString(type)
        parcel.writeString(updated_at)
        parcel.writeString(video)
        parcel.writeParcelable(watch_time, flags)
        parcel.writeInt(workout_section_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Child> {
        override fun createFromParcel(parcel: Parcel): Child {
            return Child(parcel)
        }

        override fun newArray(size: Int): Array<Child?> {
            return arrayOfNulls(size)
        }
    }
}


data class Files(
    val created_at: String = "",
    val description: String = "",
    val file_name: String = "",
    val file_type: String = "",
    val folder_id: Int = 0,
    val id: Int = 0,
    val media_type: String = "",
    val name: String = "",
    val thumbnail: String = "",
    val thumbnail_path: String = "",
    val updated_at: String = "",
    val user_id: Int = 0,
    val video_path: String = ""
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
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: ""
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(created_at)
        parcel.writeString(description)
        parcel.writeString(file_name)
        parcel.writeString(file_type)
        parcel.writeInt(folder_id)
        parcel.writeInt(id)
        parcel.writeString(media_type)
        parcel.writeString(name)
        parcel.writeString(thumbnail)
        parcel.writeString(thumbnail_path)
        parcel.writeString(updated_at)
        parcel.writeInt(user_id)
        parcel.writeString(video_path)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Files> {
        override fun createFromParcel(parcel: Parcel): Files {
            return Files(parcel)
        }

        override fun newArray(size: Int): Array<Files?> {
            return arrayOfNulls(size)
        }
    }
}

data class WatchTime(
    val elapsed_set: Int = 0,
    val elapsed_time: Int = 0,
    val no_of_set: String = "",
    val percentage_completed: String = "",
    val total_time: Int = 0,
    val type: String =""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: ""
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(elapsed_set)
        parcel.writeInt(elapsed_time)
        parcel.writeString(no_of_set)
        parcel.writeString(percentage_completed)
        parcel.writeInt(total_time)
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WatchTime> {
        override fun createFromParcel(parcel: Parcel): WatchTime {
            return WatchTime(parcel)
        }

        override fun newArray(size: Int): Array<WatchTime?> {
            return arrayOfNulls(size)
        }
    }
}