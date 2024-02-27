package com.s_ymb.numbergame.data
/*
    ９×９の各セルの属性情報
 */
data class CellData (
    var num: Int = 0,               // 番号
    var init: Boolean = false       // 新規時に設定されたか
)

