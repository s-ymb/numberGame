package com.s_ymb.numbergame.data

open class NumbergameData {
    companion object {
        const val NUM_NOT_SET: Int = 0
//        const val IMPOSSIBLE_NUM: Int = -1
        const val NUM_OF_COL: Int = 9             // 全体で９行
        const val NUM_OF_ROW: Int = 9             // 全体で９列
//        const val NUM_OF_SQR: Int = 9             // 全体で９枠
        const val SQR_SIZE: Int = 3               //  平方領域は３×３マス
        const val KIND_OF_DATA: Int = 9           //  マスに入る数値は１～９（０は未設定扱い）
        const val MAX_NUM_CNT: Int = 9            //　各数字は全体で９個まで
        const val IMPOSSIBLE_IDX = -1             //ありえないインデックス値 ここで定義すべきではないが…
    }

    /*
    コピーのデータ配列に指定された位置、値が設定できるかチェックする
*/
    protected fun checkData(targetData: Array<Array<Int>>, row: Int, col: Int, newNum: Int): dupErr {
        val copyData: Array<Array<Int>> = Array(NUM_OF_ROW) { Array(NUM_OF_COL) { 0 } }
        for (rowIdx in 0 until NUM_OF_ROW) {
            for (colIdx in 0 until NUM_OF_COL) {
                copyData[rowIdx][colIdx] = targetData[rowIdx][colIdx]
            }
        }
        // 今回セットする位置に値を設定してみて重複チェックを行う
        copyData[row][col] = newNum
        // 行重複チェック(変更対象のエリアのみチェック)
        if (!rowCheck(copyData, row)) {
            return dupErr.ROW_DUP
        }
        // 列重複チェック(変更対象のエリアのみチェック)
        if (!colCheck(copyData, col)) {
            return dupErr.COL_DUP
        }
        // 平方重複チェック(変更対象のエリアのみチェック)
        if (!sqrCheck(copyData, row, col)) {
            return dupErr.SQ_DUP
        }
        return dupErr.NO_DUP
    }

    /*
        行重複チェック
    */
    private fun rowCheck(checkGrid: Array<Array<Int>>, checkRowIdx: Int): Boolean {
        val oneRow = Array(NUM_OF_COL) { NUM_NOT_SET }               // １行分のデータ配列
        // チェック対象行の値を取得
        for (colIdx in 0 until NUM_OF_COL) {
            oneRow[colIdx] = checkGrid[checkRowIdx][colIdx]
        }
        // 重複チェック
        return dupCheck(oneRow)
    }

    /*
        列重複チェック
    */
    private fun colCheck(checkGrid: Array<Array<Int>>, checkColIdx: Int): Boolean {
        val oneCol = Array(NUM_OF_ROW) { 0 }           // １列分のデータ配列
        // チェック対象列の値を取得
        for (rowIdx in 0 until NUM_OF_COL) {
            oneCol[rowIdx] = checkGrid[rowIdx][checkColIdx]
        }
        // 重複チェック
        return dupCheck(oneCol)
    }

    /*
        平方重複チェック
     */
    private fun sqrCheck(
        checkGrid: Array<Array<Int>>,
        checkRowIdx: Int,
        checkColIdx: Int
    ): Boolean {
        val sqrStartRow: Int = (checkRowIdx / SQR_SIZE) * SQR_SIZE      //平方の左上のセルの行番号
        val sqrStartCol: Int = (checkColIdx / SQR_SIZE) * SQR_SIZE      //平方の左上のセルの列番号
        val oneSqr = Array(SQR_SIZE * SQR_SIZE) { 0 }     //平方内のデータ配列
        // チェック対象 平方内の値を取得
        for(rowIdx in 0 until SQR_SIZE){
            for(colIdx in 0 until SQR_SIZE) {
                oneSqr[rowIdx * SQR_SIZE + colIdx] = checkGrid[sqrStartRow + rowIdx][sqrStartCol + colIdx]
            }
        }

/*      //w
        for (rowIdx in sqrStartRow until sqrStartRow + SQR_SIZE) {
            for (coIIdx in sqrStartCol until sqrStartCol + SQR_SIZE) {
                oneSqr[checkIdx] = checkGrid[rowIdx][coIIdx]
                checkIdx++
            }
        }

 */
        // 重複チェック
        return dupCheck(oneSqr)

    }

    /*
        各ブロック要素に０以外で同じ数字が存在するかチェック
    */
    private fun dupCheck(checkArray: Array<Int>): Boolean {
        //数値は”なし”で初期化
        val isExist = BooleanArray(KIND_OF_DATA + 1) { false }  //NUM_NOT_SET も種類の１つなので＋１
        // 各行に０以外で同じ数字が存在するかチェック
        for (num in checkArray) {
            if (isExist[num]) {
                // num番目の要素はすでに存在していて、未設定値との比較以外では重複と判定
                if (num != NUM_NOT_SET) {
                    return false
                }
            }
            // num番目の配列は存在する
            isExist[num] = true
        }
        //重複なし
        return true
    }

}