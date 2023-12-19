package com.cp.brittany.dixon.activities.home.home_models

import com.cp.brittany.dixon.activities.home.workout_detail.WorkoutSection

data class WorkoutLessonsModel(
    val `data`: List<WorkoutSection>,
    val message: String,
    val status: String
)