package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DateIdeaDao {
    @Query("SELECT * FROM date_ideas ORDER BY id DESC")
    fun getAllDateIdeas(): Flow<List<DateIdeaEntity>>

    @Query("SELECT * FROM date_ideas WHERE isSavedToBucketList = 1 ORDER BY id DESC")
    fun getBucketListIdeas(): Flow<List<DateIdeaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIdea(idea: DateIdeaEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ideas: List<DateIdeaEntity>)

    @Update
    suspend fun updateIdea(idea: DateIdeaEntity)

    @Query("UPDATE date_ideas SET isSavedToBucketList = :saved WHERE id = :id")
    suspend fun updateBucketListStatus(id: Int, saved: Boolean)
}

@Dao
interface PlaylistSongDao {
    @Query("SELECT * FROM playlist_songs ORDER BY id DESC")
    fun getAllSongs(): Flow<List<PlaylistSongEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: PlaylistSongEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(songs: List<PlaylistSongEntity>)

    @Query("DELETE FROM playlist_songs WHERE id = :id")
    suspend fun deleteSong(id: Int)
}

@Dao
interface MemoryDao {
    @Query("SELECT * FROM memories ORDER BY id DESC")
    fun getAllMemories(): Flow<List<MemoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(memories: List<MemoryEntity>)

    @Query("UPDATE memories SET heartCount = heartCount + 1 WHERE id = :id")
    suspend fun addHeart(id: Int)
}

@Dao
interface GameScoreDao {
    @Query("SELECT * FROM game_scores")
    fun getAllScores(): Flow<List<GameScoreEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(score: GameScoreEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(scores: List<GameScoreEntity>)

    @Query("UPDATE game_scores SET gamesPlayed = gamesPlayed + 1, partnerAWins = partnerAWins + :addAWins, partnerBWins = partnerBWins + :addBWins WHERE gameKey = :gameKey")
    suspend fun recordGameWin(gameKey: String, addAWins: Int, addBWins: Int)
}
