package com.s_ymb.numbergame.data

import kotlinx.coroutines.flow.Flow

interface SavedGridTblRepository {
    fun getAllGrids(): Flow<List<SavedGridTbl>>

    fun getGrid(id: Int): Flow<SavedGridTbl?>

    fun get(id: Int): SavedGridTbl?

    suspend fun insert(savedGridTbl: SavedGridTbl)

    fun delete(id: Int)
}