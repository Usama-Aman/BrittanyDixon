package com.cp.brittany.dixon.activities.home.start_workout

data class PlayWorkoutModel(
    var video_path: String = "", // Url of the video
    var thumbnail_path: String = "", // Thumbnail url of the video
    var is_rest: Int = 0,
    var lesson_id: Int = 0,
    var reps: String = "", // No of reps, not linked with how many times a video will be played.
    var duration: String = "", // Duration of the video
    var video_id: Int = 0,
    var video_name: String = "",
    var no_of_sets: String = "", //it indicates the how many time a set will be played and videos in that set.
    var type: String = "", // single, child, set
    var workout_section_id: Int = 0,
    var elapsed_set: Int = 0, // No of sets completed
    var elapsed_time: Int = 0, // time of the video completed in case of single type
    var section_name: String = "",
    var section_image: String = "",
    var workout_name: String = "",
    var current_set_count: Int = 0,
    var isNewSet: Boolean = false,
    var set_id : Int = 0 ,// ID of the set
)