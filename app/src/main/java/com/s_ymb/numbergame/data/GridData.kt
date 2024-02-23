package com.s_ymb.numbergame.data

import com.s_ymb.numbergame.data.dupErr.ANY_DUP
import com.s_ymb.numbergame.data.dupErr.NO_DUP
import kotlin.random.Random

class GridData() : NumbergameData() {
    val data: Array<Array<CellData>> =
        Array(NUM_OF_ROW) { Array(NUM_OF_COL) { CellData(0, false) } }
    /*
        データの指定位置に指定データが設定可能か判定して可能な場合、値を設定する
        同じ数字が範囲内に重複する場合、範囲に応じたエラーを返す。
        （初期値の上書き判定は行わない → view で制限をかける）
     */

    public fun setData(row: Int, col: Int, newNum: Int, isInit: Boolean): dupErr {
        if(data[row][col].init){
            return ANY_DUP      //後で直す
        }

        // いままでに設定されている値をコピー
        val tmp: Array<Array<Int>> = Array(NUM_OF_ROW) { Array(NUM_OF_COL) { 0 } }
        for ((rowIdx, colArray) in data.withIndex()) {
            for ((colIdx, cell) in colArray.withIndex()) {
                tmp[rowIdx][colIdx] = cell.num
            }
        }
        val dataCheckResult: dupErr = checkData(tmp, row, col, newNum)
        if (NO_DUP == dataCheckResult) {
            //チェックOKの場合、データに反映する。
            data[row][col].num = newNum
            data[row][col].init = isInit
        }
        return dataCheckResult
    }

    /*

        数値配列の未設定(0)を範囲内(1～9)で順次設定していき、ルール満たす組み合わせの数を
        再帰的にチェックしていく。
     */
    private fun findAnswerRecursive(tmp: Array<Array<Int>>, endByFind: Boolean): MutableList<Array<Array<Int>>> {
        val retList: MutableList<Array<Array<Int>>> = mutableListOf()   //見つけた正解リスト

        // 未設定列を探し、
        for (rowIdx: Int in 0 until NUM_OF_ROW) {
            for (colIdx in 0 until NUM_OF_COL) {
                if (tmp[rowIdx][colIdx] == NUM_NOT_SET) {
                    // 未設定列を見つけたら
                    for (setNum in 1..KIND_OF_DATA) {
                        // 1 ～ 9 の数字を試してみる
                        val ret: dupErr = checkData(tmp, rowIdx, colIdx, setNum)
                        if (NO_DUP == ret) {
                            // 値をセットして次の再帰呼びだし
                            tmp[rowIdx][colIdx] = setNum
                            retList.addAll(findAnswerRecursive(tmp, endByFind))
                            if (endByFind && retList.size > 0) {
                                return retList
                            }
                            // 回答数が取得出来たら元に戻す
                            tmp[rowIdx][colIdx] = NUM_NOT_SET
                        }
                    }
                    // 試してみた結果の回答リストを返却（入力値：1 ～ 9 全て重複が発生するなら 回答数は0
                    return retList
                }
            }
        }
        //検証用
        /* Log.d("TAG", "正解あり")        //検証用
        for (rowIdx: Int in 0 until NUM_OF_ROW) {
            var lineStr = ""
            for (colIdx in 0 until NUM_OF_COL) {
                lineStr += tmp[rowIdx][colIdx].toString()
            }
            Log.d("TAG", lineStr.toString())        //検証用
        } */

        // ここで数字配列の実態を作成
        val retArr = Array(NUM_OF_ROW){Array(NUM_OF_COL){0}}
        for ((rowIdx, colArray) in data.withIndex()) {
            for ((colIdx) in colArray.withIndex()) {
                retArr[rowIdx][colIdx] = tmp[rowIdx][colIdx]
            }
        }
        retList.add(retArr)
        return retList            // 未設定データがなかったので、回答の内の１件
    }

    /*
        新規データ作成
        回答配列(satisfiedArray)より指定個数の数字(fixCellSelected)を問題データとして設定する
    */
    public fun newGame(satisfiedArray: Array<IntArray>, fixCellSelected: Int) {

        // ランダムな位置にランダムな数字を配置（すでに埋まっている場合は次）
        var seedRow: Int
        var seedCol: Int
        // データ全消去
        clearAll(true)

        // ランダムに指定個数データを設定

        var fixCelCnt = 0
        while (fixCelCnt <= fixCellSelected) {
            seedRow = Random.nextInt(NUM_OF_ROW)
            seedCol = Random.nextInt(NUM_OF_COL)
            if (!data[seedRow][seedCol].init) {
                data[seedRow][seedCol].num = satisfiedArray[seedRow][seedCol]
                data[seedRow][seedCol].init = true
                fixCelCnt++
            }
        }
    }

    public fun resumeGame(newData: Array<Array<CellData>>){
        for ((rowIdx, colArray) in data.withIndex()) {
            for ((colIdx) in colArray.withIndex()) {
                data[rowIdx][colIdx].num = newData[rowIdx][colIdx].num
                data[rowIdx][colIdx].init = newData[rowIdx][colIdx].init
            }
        }
    }

    /*
        指定セルに指定された場合の正解の数を返却する
    */

    public fun searchAnswer(checkRowId: Int, checkColId: Int, checkNum: Int) : MutableList<Array<Array<Int>>>{
        var retList: MutableList<Array<Array<Int>>> = mutableListOf()
        // いままでに設定されている値をコピー
        val tmp = Array(NUM_OF_ROW){Array(NUM_OF_COL){0}}
        for ((rowIdx, colArray) in data.withIndex()) {
            for ((colIdx, cell) in colArray.withIndex()) {
                tmp[rowIdx][colIdx] = cell.num
            }
        }
        // 指定セルに値を設定してみて回答が存在しているかチェック
        val ret: dupErr = checkData(tmp, checkRowId, checkColId, checkNum)
        if (NO_DUP == ret) {
            // 値をセットして次の再帰呼びだし
            tmp[checkRowId][checkColId] = checkNum
            retList = findAnswerRecursive(tmp, false)
        }
        return retList
    }


    /*
    全ての要素を初期化
    */
    public fun clearAll (withFixCell: Boolean) {
        for ((rowIdx, colArray) in data.withIndex()) {
            for ((colIdx) in colArray.withIndex()) {
                // 全データクリアの場合もしくは初期列以外の場合
                if((withFixCell) || !data[rowIdx][colIdx].init) {
                    // データを初期化
                    data[rowIdx][colIdx].num = NUM_NOT_SET
                    data[rowIdx][colIdx].init = false
                }
            }
        }
    }
}

