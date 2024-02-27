package io.github.s_ymb.numbergame.data

import kotlinx.coroutines.flow.Flow

class OfflineSavedTblRepository(private val savedTableDao: SavedTblDao ): SavedTblRepository
{
    override fun getAllGrids(): Flow<List<SavedTbl>> = savedTableDao.getAllGrids()
    override fun getAll(): List<SavedTbl> = savedTableDao.getAll()

    override fun getGrid(id: Int): Flow<SavedTbl?> = savedTableDao.getGrid(id)

    override fun get(id: Int): SavedTbl?= savedTableDao.get(id)

    override suspend fun insert(savedTbl: SavedTbl) = savedTableDao.insert(savedTbl)

    override fun getCnt(id: Int): Int = savedTableDao.getCnt(id)

    override fun delete(id: Int) = savedTableDao.delete(id)
}