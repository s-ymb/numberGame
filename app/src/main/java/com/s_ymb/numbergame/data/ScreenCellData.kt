package com.s_ymb.numbergame.data

data class ScreenCellData(
    var num: Int,
    var init: Boolean,                  //初期データか？
    var isSelected : Boolean,           //選択中のセルか？
    var isSameNum : Boolean             //選択中のセルの数字と同じか？
)
