package io.github.s_ymb.numbergame.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedCellTblDao {

    @Query("SELECT * FROM SavedCellTbl ORDER BY id, row_id , col_id ")
    fun getAllGrids(): Flow<List<SavedCellTbl>>

    @Query("SELECT * FROM SavedCellTbl ORDER BY id, row_id, col_id")
    fun getAll(): List<SavedCellTbl>

    @Query("SELECT * FROM SavedCellTbl WHERE id = :id ORDER BY id, row_id, col_id")
    fun getGrids(id: Int): Flow<List<SavedCellTbl>>

    @Query("SELECT * FROM SavedCellTbl WHERE id = :id ORDER BY id, row_id, col_id")
    fun get(id: Int): List<SavedCellTbl>

    @Query("SELECT count(*) FROM SavedCellTbl WHERE id = :id")
    fun getCnt(id: Int): Int

    @Insert(onConflict = OnConflictStrategy.ABORT )
    suspend fun insert(savedCellTbl: SavedCellTbl)

    @Query("DELETE FROM SavedCellTbl WHERE id = :id")
    fun delete(id: Int)
}