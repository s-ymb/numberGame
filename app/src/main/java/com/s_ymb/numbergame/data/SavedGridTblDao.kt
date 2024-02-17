package com.s_ymb.numbergame.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
@Dao
interface SavedGridTblDao {

    @Query("SELECT * FROM SavedGridTbl ORDER BY id ASC")
    fun getAllGrids(): Flow<List<SavedGridTbl>>

    @Query("SELECT * FROM SavedGridTbl WHERE id = :id")
    fun getGrid(id: Int): Flow<SavedGridTbl?>

    @Query("SELECT * FROM SavedGridTbl WHERE id = :id")
    fun get(id: Int): SavedGridTbl?

    @Insert(onConflict = OnConflictStrategy.ABORT )
    suspend fun insert(grid: SavedGridTbl)

    @Query("DELETE FROM SavedGridTbl WHERE id = :id")
    fun delete(id: Int)

}