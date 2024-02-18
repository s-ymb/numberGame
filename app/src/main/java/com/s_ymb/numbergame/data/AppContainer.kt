package com.s_ymb.numbergame.data

import android.content.Context

interface AppContainer {
    val satisfiedGridTblRepository: SatisfiedGridTblRepository
    val savedCellTblRepository: SavedCellTblRepository
    val savedTblRepository: SavedTblRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineItemsRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [ItemsRepository]
     */
    override val satisfiedGridTblRepository: SatisfiedGridTblRepository by lazy {
        OfflineSatisfiedGridTblRepository(SatisfiedGridDataBase.getDatabase(context).satisfiedGridTblDao())
    }
    override val savedCellTblRepository: SavedCellTblRepository by lazy {
        OfflineSavedCellTblRepository(SavedDataBase.getDatabase(context).savedCellTblDao())
    }
    override val savedTblRepository: SavedTblRepository by lazy {
        OfflineSavedTblRepository(SavedDataBase.getDatabase(context).savedTblDao())
    }
}
