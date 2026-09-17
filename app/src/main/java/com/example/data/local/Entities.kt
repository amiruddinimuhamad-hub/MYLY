package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "date_ideas")
data class DateIdeaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val category: String,
    val estimatedCost: String,
    val duration: String,
    val isSavedToBucketList: Boolean = false,
    val isCompleted: Boolean = false
)

@Entity(tableName = "playlist_songs")
data class PlaylistSongEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val artist: String,
    val dedicatedBy: String,
    val whyThisSong: String,
    val platform: String,
    val externalUrl: String,
    val addedDate: String
)

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val note: String,
    val dateString: String,
    val locationName: String,
    val heartCount: Int = 0
)

@Entity(tableName = "game_scores")
data class GameScoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val gameKey: String,
    val gameTitle: String,
    val gamesPlayed: Int,
    val partnerAWins: Int,
    val partnerBWins: Int
)
