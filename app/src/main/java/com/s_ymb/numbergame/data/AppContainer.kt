package com.s_ymb.numbergame.data

import android.content.Context

interface AppContainer {
    val satisfiedGridTblRepository: SatisfiedGridTblRepository
    val savedCellTblRepository: SavedCellTblRepository
    val savedTblRepository: SavedTblRepository
}

/**
 *  Room データベースのインスタンスを所有するクラス
 *  ・正解情報
 *  []satisfiedGridTblRepository]
 *  ・保存概要情報（一時保存時にその時の概要情報を保存）
 *  [savedCellTblRepository]
 *  ・保存詳細情報（一時保存時にセル単位の詳細情報を保存）
 *  [savedTblRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
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
