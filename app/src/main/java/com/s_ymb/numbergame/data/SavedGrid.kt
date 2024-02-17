package com.s_ymb.numbergame.data

data class SavedGrid(
    val createDt: String = "",
    val createUser: String = "",
    val data: Array<Array<CellData>> = Array(NumbergameData.NUM_OF_ROW){Array(NumbergameData.NUM_OF_COL){CellData()}}
)
