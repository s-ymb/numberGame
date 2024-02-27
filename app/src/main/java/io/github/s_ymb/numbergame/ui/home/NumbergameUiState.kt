package io.github.s_ymb.numbergame.ui.home

import io.github.s_ymb.numbergame.data.NumbergameData
import io.github.s_ymb.numbergame.data.ScreenBtnData
import io.github.s_ymb.numbergame.data.ScreenCellData
import io.github.s_ymb.numbergame.data.dupErr

data class NumbergameUiState(
    val currentData: Array<Array<ScreenCellData>> = Array(NumbergameData.NUM_OF_ROW)
                        { Array(NumbergameData.NUM_OF_COL)
                            {
                                ScreenCellData(NumbergameData.NUM_NOT_SET, init = false, isSelected = false, isSameNum =  false)
                            }
                        },
    val currentBtn: Array<ScreenBtnData> = Array(NumbergameData.KIND_OF_DATA + 1){ScreenBtnData(0)},
    val haveSearchResult: Boolean = false,
    val currentSearchResult: Array<Int> = Array(NumbergameData.KIND_OF_DATA + 1) { 0 },
    val isGameOver: Boolean = false,
    val errBtnMsgID:dupErr = dupErr.NO_DUP,
    val errBtnNum: Int = 0,
    val fixCellCnt: Int = 0,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NumbergameUiState

        if (!currentData.contentDeepEquals(other.currentData)) return false
        if (!currentBtn.contentEquals(other.currentBtn)) return false
        return currentSearchResult.contentEquals(other.currentSearchResult)
    }

    override fun hashCode(): Int {
        var result = currentData.contentDeepHashCode()
        result = 31 * result + currentBtn.contentHashCode()
        result = 31 * result + currentSearchResult.contentHashCode()
        return result
    }
}
