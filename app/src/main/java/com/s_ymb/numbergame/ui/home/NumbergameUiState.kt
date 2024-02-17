package com.s_ymb.numbergame.ui.home

import com.s_ymb.numbergame.data.CellData
import com.s_ymb.numbergame.data.NumbergameData
import com.s_ymb.numbergame.data.ScreenBtnData
import com.s_ymb.numbergame.data.ScreenCellData
import com.s_ymb.numbergame.data.dupErr

data class NumbergameUiState(
    val currentData: Array<Array<ScreenCellData>> = Array(NumbergameData.NUM_OF_ROW) { Array(NumbergameData.NUM_OF_COL) { ScreenCellData(CellData.NUM_NOT_SET, false, false, false) } },
    val currentBtn: Array<ScreenBtnData> = Array(NumbergameData.KIND_OF_DATA + 1){ScreenBtnData(0)},
    val haveSearchResult: Boolean = false,
    val currentSearchResult: Array<Int> = Array(NumbergameData.KIND_OF_DATA + 1) { 0 },
    val isGameOver: Boolean = false,
    val errBtnMsgID:dupErr = dupErr.NO_DUP,
    val errBtnNum: Int = 0,
    val fixCellCnt: Int = 0,
)
