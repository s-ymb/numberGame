package com.s_ymb.numbergame.data

import kotlinx.coroutines.flow.Flow

interface SavedCellTblRepository {
    fun getAllGrids(): Flow<List<SavedCellTbl>>

    fun getAll(): List<SavedCellTbl>

    fun getGrids(id: Int): Flow<List<SavedCellTbl>>

    fun get(id: Int): List<SavedCellTbl>

    fun getCnt(id: Int): Int

    suspend fun insert(savedCellTbl: SavedCellTbl)

    fun delete(id: Int)

}