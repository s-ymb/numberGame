package com.s_ymb.numbergame.data

data class CellData (
var num: Int = 0,
var init: Boolean = false
){
companion object {
    //定数定義
    const val NUM_NOT_SET: Int = 0
    const val IMPOSSIBLE_NUM: Int = -1
}
}
