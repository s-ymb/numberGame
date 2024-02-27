package io.github.s_ymb.numbergame.data

import kotlinx.coroutines.flow.Flow

class OfflineSavedCellTblRepository(private val savedCellTblDao: SavedCellTblDao ): SavedCellTblRepository{
    override fun getAllGrids(): Flow<List<SavedCellTbl>> = savedCellTblDao.getAllGrids()

    override fun getAll(): List<SavedCellTbl> = savedCellTblDao.getAll()

    override fun getGrids(id: Int): Flow<List<SavedCellTbl>> = savedCellTblDao.getGrids(id)

    override fun get(id: Int): List<SavedCellTbl> = savedCellTblDao.get(id)

    override fun getCnt(id: Int): Int = savedCellTblDao.getCnt(id)

    override suspend fun insert(savedCellTbl: SavedCellTbl) = savedCellTblDao.insert(savedCellTbl)

    override fun delete(id: Int) = savedCellTblDao.delete(id)       // 不要？

}