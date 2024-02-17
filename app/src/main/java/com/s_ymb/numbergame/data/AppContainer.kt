package com.s_ymb.numbergame.data

import android.content.Context

interface AppContainer {
    val satisfiedGridTblRepository: SatisfiedGridTblRepository
    val savedGridTblRepository: SavedGridTblRepository
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
    override val savedGridTblRepository: SavedGridTblRepository by lazy {
        OfflineSavedGridTblRepository(SavedGridDataBase.getDatabase(context).savedGridTblDao())
    }
}
