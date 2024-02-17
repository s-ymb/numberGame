package com.s_ymb.numbergame.data

data class SatisfiedGrid (
    val createDt: String = "",
    val createUser: String = "",
    val data: Array<IntArray> = Array(NumbergameData.NUM_OF_ROW){IntArray(NumbergameData.NUM_OF_COL){NumbergameData.NUM_NOT_SET}}
)


