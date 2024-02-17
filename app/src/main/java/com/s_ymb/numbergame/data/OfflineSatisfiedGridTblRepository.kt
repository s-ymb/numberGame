package com.s_ymb.numbergame.data

import kotlinx.coroutines.flow.Flow

class OfflineSatisfiedGridTblRepository(private val satisfiedGridTableDao: SatisfiedGridTblDao ): SatisfiedGridTblRepository{
    override fun getAllGrids(): Flow<List<SatisfiedGridTbl>> = satisfiedGridTableDao.getAllGrids()

    // Flow 以外で戻したい為
    override fun getAll(): List<SatisfiedGridTbl> = satisfiedGridTableDao.getAll()

    override fun getGrid(data: String): Flow<SatisfiedGridTbl?> = satisfiedGridTableDao.getGrid(data)

    override fun get(data: String): SatisfiedGridTbl? = satisfiedGridTableDao.get(data)

    override fun getCnt(data: String): Int = satisfiedGridTableDao.getCnt(data)

    override suspend fun insert(grid: SatisfiedGridTbl) = satisfiedGridTableDao.insertSatisfiedGrid(grid)

    override fun delete(gridData: String) = satisfiedGridTableDao.delete(gridData)
}