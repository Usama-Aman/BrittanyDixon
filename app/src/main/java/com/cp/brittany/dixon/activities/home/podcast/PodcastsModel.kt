package com.cp.brittany.dixon.activities.home.podcast

data class PodcastsModel(
    val `data`: List<PodcastsData>,
    val message: String,
    val status: String
)

data class PodcastsData(
    val album_name: String,
    val artist_name: String,
    val created_at: String,
    val duration: Int,
    val id: Int,
    val insight_category_id: String,
    val name: String,
    val podcast_audio: String,
    val podcast_audio_path: String,
    val podcast_thumbnail: String,
    val podcast_thumbnail_path: String,
    val updated_at: String,
    var isPlaying: Boolean = false,
)