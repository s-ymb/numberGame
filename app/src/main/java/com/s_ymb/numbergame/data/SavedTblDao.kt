package com.s_ymb.numbergame.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedTblDao {

    @Query("SELECT * FROM SavedTbl ORDER BY id ")
    fun getAllGrids(): Flow<List<SavedTbl>>

    @Query("SELECT * FROM SavedTbl ORDER BY id")
    fun getAll(): List<SavedTbl>

    @Query("SELECT * FROM SavedTbl WHERE id = :id")
    fun getGrid(id: Int): Flow<SavedTbl?>

    @Query("SELECT * FROM SavedTbl WHERE id = :id")
    fun get(id: Int): SavedTbl?

    @Query("SELECT count(*) FROM SavedTbl WHERE id = :id")
    fun getCnt(id: Int): Int

    @Insert(onConflict = OnConflictStrategy.ABORT )
    suspend fun insert(savedTbl: SavedTbl) : Long

    @Query("DELETE FROM SavedTbl WHERE id = :id")
    fun delete(id: Int)

}