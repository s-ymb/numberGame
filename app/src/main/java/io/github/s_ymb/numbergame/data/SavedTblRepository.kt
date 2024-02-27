package io.github.s_ymb.numbergame.data

import kotlinx.coroutines.flow.Flow

interface SavedTblRepository {
    fun getAllGrids(): Flow<List<SavedTbl>>

    fun getAll(): List<SavedTbl>

    fun getGrid(id: Int): Flow<SavedTbl?>

    fun get(id: Int): SavedTbl?

    fun getCnt(id: Int): Int

    suspend fun insert(savedTbl: SavedTbl) : Long

    fun delete(id: Int)

}