package io.github.s_ymb.numbergame.data

import kotlinx.coroutines.flow.Flow

class OfflineSatisfiedGridTblRepository(private val satisfiedGridTableDao: SatisfiedGridTblDao ): SatisfiedGridTblRepository{
    override fun getAllGrids(): Flow<List<SatisfiedGridTbl>> = satisfiedGridTableDao.getAllGrids()

    // Flow 以外で戻したい為
    override fun getAll(): List<SatisfiedGridTbl> = satisfiedGridTableDao.getAll()

    override fun getGrid(data: String): Flow<SatisfiedGridTbl?> = satisfiedGridTableDao.getGrid(data)

    override fun get(data: String): SatisfiedGridTbl? = satisfiedGridTableDao.get(data)

    override fun getCnt(data: String): Int = satisfiedGridTableDao.getCnt(data)

    override suspend fun insert(satisfiedGridTbl: SatisfiedGridTbl) = satisfiedGridTableDao.insertSatisfiedGrid(satisfiedGridTbl)

    override fun delete(data: String) = satisfiedGridTableDao.delete(data)
}