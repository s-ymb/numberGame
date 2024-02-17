package com.s_ymb.numbergame.data

import kotlinx.coroutines.flow.Flow

class OfflineSavedGridTblRepository(private val savedGridTableDao: SavedGridTblDao ): SavedGridTblRepository
{
    override fun getAllGrids(): Flow<List<SavedGridTbl>> = savedGridTableDao.getAllGrids()

    override fun getGrid(id: Int): Flow<SavedGridTbl?> = savedGridTableDao.getGrid(id)

    override fun get(id: Int): SavedGridTbl?= savedGridTableDao.get(id)

    override suspend fun insert(savedGridTbl: SavedGridTbl) = savedGridTableDao.insert(savedGridTbl)

    override fun delete(id: Int) = savedGridTableDao.delete(id)
}