package com.s_ymb.numbergame.data

enum class dupErr {
    NO_DUP,     //重複なし
    ROW_DUP,    //行で重複
    COL_DUP,    //列で重複
    SQ_DUP,     //四角いエリアで重複
    ANY_DUP     //その他何かで重複（未定義）
}