package io.github.s_ymb.numbergame.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SatisfiedGridTblDao {

    @Query("SELECT * FROM SatisfiedGridTbl ORDER BY create_dt ASC")
    fun getAllGrids(): Flow<List<SatisfiedGridTbl>>

    @Query("SELECT * FROM SatisfiedGridTbl ORDER BY create_dt ASC")
    fun getAll(): List<SatisfiedGridTbl>

    @Query("SELECT * FROM SatisfiedGridTbl WHERE gridData = :data")
    fun getGrid(data: String): Flow<SatisfiedGridTbl?>

    @Query("SELECT * FROM SatisfiedGridTbl WHERE gridData = :data")
    fun get(data: String): SatisfiedGridTbl?

    @Query("SELECT count(*) FROM SatisfiedGridTbl WHERE gridData = :data")
    fun getCnt(data: String): Int

    @Insert(onConflict = OnConflictStrategy.ABORT )
    suspend fun insertSatisfiedGrid(grid: SatisfiedGridTbl)

    @Query("DELETE FROM SatisfiedGridTbl WHERE gridData = :data")
    fun delete(data: String)
//    @Delete
//    fun delete(gridData: String)

}