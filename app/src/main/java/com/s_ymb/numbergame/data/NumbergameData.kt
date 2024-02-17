package com.s_ymb.numbergame.data

public open class NumbergameData {
    companion object {
        const val NUM_NOT_SET: Int = 0
        const val IMPOSSIBLE_NUM: Int = -1
        const val NUM_OF_COL: Int = 9             //   グリッドは９×９マス
        const val NUM_OF_ROW: Int = 9             //
        const val SQR_SIZE: Int = 3                 //  平方領域は３×３マス
        const val KIND_OF_DATA: Int = 9           //  マスに入る数値は１～９（０は未設定扱い）
        const val MAX_NUM_CNT: Int = 9            //　各数字は全体で９個まで
        const val IMPOSSIBLE_IDX = -1             //ありえないインデックス値 ここで定義すべきではないが…
    }
}