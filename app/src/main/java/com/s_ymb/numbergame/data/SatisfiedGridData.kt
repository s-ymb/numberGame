package com.s_ymb.numbergame.data

/**
 *     NumberGame の定義を満たす数列のクラス
 *
 */
class SatisfiedGridData : NumbergameData() {

    private val dataList: MutableList<SatisfiedGrid> = SatisfiedGriListInit.getInitialListData()

    fun add(satisfied :SatisfiedGrid =SatisfiedGrid() ){
        dataList.add(satisfied)
    }


/*
    正解データを正解リストからランダムに選択し、セルの位置の再配置を指定する
*/
/*    enum class RotateType(type: Int){
        rowRotate(0),
        colRotate(1),
        sqrRotate(2),
    }
 */
    /*
        データ入替の種類
     */
    enum class RotateType{
        LINE_ROTATE,     //行入替
        AREA_ROTATE,     //９×９のエリア入替
    }

    enum class RotateDirection{
        ROW,        // 行入替
        COL         // 列入替
    }

    enum class RotatePattern{
        PATTERN_12,        // １行（列）と２行（列）を入替
        PATTERN_13,        // １行（列）と３行（列）を入替
        PATTERN_23         // ２行（列）と３行（列）を入替
    }

    enum class RotateArea{
        START,      // 行入替の場合、最上段のエリア、列入替の場合、左端のエリア
        MIDDLE,     // 真ん中のエリア
        END         // 行入替の場合、最下段のエリア、列入替の場合、右端のエリア
    }

    fun getSatisfied() : Array<IntArray>  {
        // 正解リストのリストより１つの正解を選択
        val satisfiedIdx: Int=  (0 until dataList.size).random()
        val satisfiedGrid = dataList[satisfiedIdx]

        // 入れ替えるパターンをランダムに選択
        val rotateType = (RotateType.values()).random().ordinal
        val rotateDirection = (RotateDirection.values()).random().ordinal
        val rotateArea = (RotateArea.values()).random().ordinal
        val rotatePattern = (RotatePattern.values()).random().ordinal
        val offset: Array<IntArray> = getOffset(
                                        type = rotateType,
                                        direction = rotateDirection,
                                        pattern = rotatePattern,
                                        area = rotateArea
                                    )

        // １～９のランダムな順列を生成
        val seedArray = (1..NumbergameData.KIND_OF_DATA).shuffled()

        // 正解リストの値を生成する数字のIndex番号として入替パターンのオフセット分ずらした位置に配置する
        val retArray: Array<IntArray> = Array(NumbergameData.NUM_OF_ROW) { IntArray(NumbergameData.NUM_OF_COL) { NumbergameData.NUM_NOT_SET } }
        for (rowIdx in 0 until NumbergameData.NUM_OF_ROW) {
            for (colIdx in 0 until NumbergameData.NUM_OF_COL) {
                if (rotateDirection == RotateDirection.ROW.ordinal) {
                    // 行入替パターンの場合、設定元の行番号をオフセット分ずらした値を設定する
                    retArray[rowIdx][colIdx] =
                        seedArray[satisfiedGrid.data[rowIdx + offset[rowIdx][colIdx]][colIdx] - 1]        // 配列は０オリジンなので１ずらす
                } else {
                    // 列入替パターンの場合、設定元の列番号をオフセット分ずらした値を設定する
                    retArray[rowIdx][colIdx] =
                        seedArray[satisfiedGrid.data[rowIdx][colIdx + offset[rowIdx][colIdx]] - 1]        // 配列は０オリジンなので１ずらす
                }
            }
        }
        return retArray
    }

    private fun getOffset(type: Int, direction: Int, pattern: Int,area: Int): Array<IntArray> {
        val retOffset =
            Array(NumbergameData.NUM_OF_ROW) { IntArray(NumbergameData.NUM_OF_COL) { NumbergameData.NUM_NOT_SET } }
        if (type == RotateType.LINE_ROTATE.ordinal) {
            // 行 or 列 入替の場合
            if (direction == RotateDirection.ROW.ordinal) {
                //行入替の場合
                //マスクのパターンを取得
                val maskOffset = getRowMask(pattern = pattern)
                //指定エリアにマスクパターンを反映する
                val areaStartRow = NumbergameData.SQR_SIZE * area
                for (maskIdx in 0 until NumbergameData.SQR_SIZE) {
                    for (colIdx in 0 until NumbergameData.NUM_OF_COL) {
                        retOffset[areaStartRow + maskIdx][colIdx] = maskOffset[maskIdx][colIdx]
                    }
                }
            } else if (direction == RotateDirection.COL.ordinal) {
                //列入替の場合
                // マスクのパターンを取得
                val maskOffset = getColMask(pattern = pattern)
                //指定エリアにマスクパターンを反映する
                val areaStartCol = NumbergameData.SQR_SIZE * area
                for (maskIdx in 0 until NumbergameData.SQR_SIZE) {
                    for (rowIdx in 0 until NumbergameData.NUM_OF_ROW) {
                        retOffset[rowIdx][areaStartCol + maskIdx] = maskOffset[rowIdx][maskIdx]
                    }
                }
            }
        } else if (type == RotateType.AREA_ROTATE.ordinal) {
            // エリア入替の場合(マスクパターン毎に、ここでセット）
            if (direction == RotateDirection.ROW.ordinal) {
                // エリアの行入替の場合
                when (pattern) {
                    RotatePattern.PATTERN_12.ordinal
                    -> {
                        // 上段のエリアと中段のエリアを入れ替える
                        // 上段エリアに３段下のエリアへの移動を設定
                        for (maskIdx in 0 until NumbergameData.SQR_SIZE) {
                            for (colIdx in 0 until NumbergameData.NUM_OF_COL) {
                                retOffset[maskIdx][colIdx] = 3
                            }
                        }
                        // ２段目のスタート行
                        val startRowIdx = NumbergameData.SQR_SIZE
                        //中段エリアに３段上のエリアへの移動を設定
                        for (maskIdx in startRowIdx until startRowIdx +NumbergameData.SQR_SIZE) {
                            for (colIdx in 0 until NumbergameData.NUM_OF_COL) {
                                retOffset[maskIdx][colIdx] = -3
                            }
                        }
                    }
                    RotatePattern.PATTERN_13.ordinal
                    -> {
                        // 上段のエリアと下段のエリアを入れ替える
                        // 上段エリアに６段下のエリアへの移動を設定
                        for (maskIdx in 0 until NumbergameData.SQR_SIZE) {
                            for (colIdx in 0 until NumbergameData.NUM_OF_COL) {
                                retOffset[maskIdx][colIdx] = 6
                            }
                        }
                        // 下段エリアに６段上のエリアへの移動を設定
                        // ３段目のスタート行
                        val startRowIdx = NumbergameData.SQR_SIZE + NumbergameData.SQR_SIZE
                        for (maskIdx in startRowIdx until startRowIdx + NumbergameData.SQR_SIZE) {
                            for (colIdx in 0 until NumbergameData.NUM_OF_COL) {
                                retOffset[maskIdx][colIdx] = -6
                            }
                        }
                    }

                    RotatePattern.PATTERN_23.ordinal
                    -> {
                        // 上段のエリアと中段のエリアを入れ替える
                        // 上段エリアに３段下のエリアへの移動を設定
                        // ２段目のスタート行
                        val start2RowIdx = NumbergameData.SQR_SIZE
                        for (maskIdx in  start2RowIdx  until  + start2RowIdx + NumbergameData.SQR_SIZE) {
                            for (colIdx in 0 until NumbergameData.NUM_OF_COL) {
                                retOffset[maskIdx][colIdx] = 3
                            }
                        }
                        // ３段目のスタート行
                        val start3RowIdx = NumbergameData.SQR_SIZE + NumbergameData.SQR_SIZE
                        //中段エリアに３段上のエリアへの移動を設定
                        for (maskIdx in start3RowIdx until start3RowIdx + NumbergameData.SQR_SIZE) {
                            for (colIdx in 0 until NumbergameData.NUM_OF_COL) {
                                retOffset[maskIdx][colIdx] = -3
                            }
                        }

                    }
                }
            } else if (direction == RotateDirection.COL.ordinal) {
                // エリアの列入替の場合
                when (pattern) {
                    RotatePattern.PATTERN_12.ordinal
                    -> {
                        // 左側のエリアと中側のエリアを入れ替える
                        // 左側エリアに３段右のエリアへの移動を設定
                        for (maskIdx in 0 until NumbergameData.SQR_SIZE) {
                            for (rowIdx in 0 until NumbergameData.NUM_OF_ROW) {
                                retOffset[rowIdx][maskIdx] = 3
                            }
                        }
                        // 中側エリアのスタート列
                        val startColIdx = NumbergameData.SQR_SIZE
                        //中段エリアに左側のエリアへの移動を設定
                        for (maskIdx in startColIdx until startColIdx + NumbergameData.SQR_SIZE) {
                            for (rowIdx in 0 until NumbergameData.NUM_OF_ROW) {
                                retOffset[rowIdx][startColIdx + maskIdx] = -3
                            }
                        }
                    }

                    RotatePattern.PATTERN_13.ordinal
                    -> {
                        // 左側のエリアと右側のエリアを入れ替える
                        // 左側エリアに６行右のエリアへの移動を設定
                        for (maskIdx in 0 until NumbergameData.SQR_SIZE) {
                            for (rowIdx in 0 until NumbergameData.NUM_OF_ROW) {
                                retOffset[rowIdx][maskIdx] = 6
                            }
                        }
                        // 右側エリアに６行右のエリアへの移動を設定
                        // 右側エリアのスタート列
                        val startColIdx = NumbergameData.SQR_SIZE + NumbergameData.SQR_SIZE
                        for (maskIdx in startColIdx until startColIdx + NumbergameData.SQR_SIZE) {
                            for (rowIdx in 0 until NumbergameData.NUM_OF_ROW) {
                                retOffset[rowIdx][maskIdx] = -6
                            }
                        }

                    }

                    RotatePattern.PATTERN_23.ordinal
                    -> {
                        // 中側のエリアと右側のエリアを入れ替える
                        // 中側エリアのスタート列
                        val start2ColIdx = NumbergameData.SQR_SIZE
                        //中段エリアに右側のエリアへの移動を設定
                        for (maskIdx in start2ColIdx until start2ColIdx + NumbergameData.SQR_SIZE) {
                            for (rowIdx in 0 until NumbergameData.NUM_OF_ROW) {
                                retOffset[rowIdx][start2ColIdx + maskIdx] = 3
                            }
                        }
                        // 右側エリアのスタート列
                        val start3ColIdx = NumbergameData.SQR_SIZE + NumbergameData.SQR_SIZE
                        //中段エリアに右側のエリアへの移動を設定
                        for (maskIdx in start3ColIdx until start3ColIdx + NumbergameData.SQR_SIZE) {
                            for (rowIdx in 0 until NumbergameData.NUM_OF_ROW) {
                                retOffset[rowIdx][start3ColIdx + maskIdx] = -3
                            }
                        }
                    }
                }
            }
        }
        return retOffset
    }

    /*
            行入替の場合の移動パターンの配列を作成する
    */
    private fun getRowMask(pattern: Int): Array<IntArray>{
        // １エリア分の行数のマスク
        val retMask = Array(NumbergameData.SQR_SIZE){IntArray(NumbergameData.NUM_OF_COL){NumbergameData.NUM_NOT_SET}}
        when(pattern) {
            RotatePattern.PATTERN_12.ordinal -> {
                // １行目と２行目の入替の場合、１行目([0])に＋１を２行目([1])にー１を設定
                for (colIdx in 0 until NumbergameData.NUM_OF_COL) {
                    retMask[0][colIdx] = 1
                    retMask[1][colIdx] = -1
                }
            }
            RotatePattern.PATTERN_13.ordinal -> {
                // １行目と３行目の入替の場合、１行目([0])に＋2を３行目([2])に-2を設定
                for (colIdx in 0 until NumbergameData.NUM_OF_COL) {
                    retMask[0][colIdx] = 2
                    retMask[2][colIdx] = -2
                }
            }
            RotatePattern.PATTERN_23.ordinal -> {
                // ２行目と３行目の入替の場合、２行目([1])に＋1を３行目([2])に-1を設定
                for (colIdx in 0 until NumbergameData.NUM_OF_COL) {
                    retMask[1][colIdx] = 1
                    retMask[2][colIdx] = -1
                }
            }
        }
        return retMask
    }

    /*
            列入替の場合の移動パターンの配列を作成する
    */
    private fun getColMask(pattern: Int): Array<IntArray> {
        val retMask = Array(NumbergameData.NUM_OF_ROW){IntArray(NumbergameData.SQR_SIZE){NumbergameData.NUM_NOT_SET}}
        when(pattern) {
            RotatePattern.PATTERN_12.ordinal -> {
                // １列目と２列目の入替の場合、１列目([0])に＋１を２列目([1])にー１を設定
                for (rowIdx in 0 until NumbergameData.NUM_OF_ROW) {
                    retMask[rowIdx][0] = 1
                    retMask[rowIdx][1] = -1
                }
            }
            RotatePattern.PATTERN_13.ordinal -> {
                // １列目と３列目の入替の場合、１列目([0])に＋2を３行列([2])に-2を設定
                for (rowIdx in 0 until NumbergameData.NUM_OF_ROW) {
                    retMask[rowIdx][0] = 2
                    retMask[rowIdx][2] = -2
                }
            }
            SatisfiedGridData.RotatePattern.PATTERN_23.ordinal -> {
                // ２列目と３列目の入替の場合、２列目([1])に＋1を３列目([2])に-1を設定
                for (rowIdx in 0 until NumbergameData.NUM_OF_ROW) {
                    retMask[rowIdx][1]= 1
                    retMask[rowIdx][2] = -1
                }
            }
        }
        return retMask
    }



    /*
    val data: Array<Array<Int>> = Array(NUM_OF_ROW) { Array(NUM_OF_COL) { 0 } }

    // 正解リストを詰めていく順番（再帰回数）０：初期値、１：１回目の再帰呼び出し…
    val satisfiedSetOrder =
        arrayOf(
            intArrayOf(0, 1, 2, 6, 7, 8, 3, 4, 5),
            intArrayOf(3, 4, 5, 0, 1, 2, 6, 7, 8),
            intArrayOf(6, 7, 8, 3, 4, 5, 0, 1, 2),
            intArrayOf(2, 0, 1, 8, 6, 7, 5, 3, 4),
            intArrayOf(5, 3, 4, 2, 0, 1, 8, 6, 7),
            intArrayOf(8, 6, 7, 5, 3, 4, 2, 0, 1),
            intArrayOf(1, 2, 0, 7, 8, 6, 4, 5, 3),
            intArrayOf(4, 5, 3, 1, 2, 0, 7, 8, 6),
            intArrayOf(7, 8, 6, 4, 5, 3, 1, 2, 0)
        )

    data class Selectable(
        var rowIdx: Int = 0,
        var colIdx: Int = 0,
        val canSelect: Array<Boolean> = Array(KIND_OF_DATA + 1) { true }
    )

    /**
     *     再帰的に正解リストを作成する
     *
     */
    public fun findSatisfiedRecursive(data: Array<Array<Int>>, depth: Int): MutableList<Array<Array<Int>>> {
        var retList: MutableList<Array<Array<Int>>> = mutableListOf()   //見つけた正解リスト
        //depth回目の再帰呼び出しで設定される欄に設定できる数値のバリエーションを求める
        var selectableArray: Array<Selectable> = Array(NUM_OF_SQR){Selectable()}
        var sqrIdx = 0
        for ((rowIdx, colArray) in satisfiedSetOrder.withIndex()) {
            for ((colIdx, order) in colArray.withIndex()) {
                if (depth == satisfiedSetOrder[rowIdx][colIdx]) {
                    // depth 回目の再帰で設定するセルの位置を設定
                    selectableArray[sqrIdx].rowIdx = rowIdx
                    selectableArray[sqrIdx].colIdx = colIdx
                    // 該当位置で設定できる数字をチェック
                    var kindOfSelectable = 0
                    for(num in 1..KIND_OF_DATA){
                        if(dupErr.NO_DUP == checkData(targetData = data, row = rowIdx, col = colIdx, newNum = num)) {
                            kindOfSelectable++
                        }else{
                            // 重複チェックで重複なし以外の場合は設定出来ないに設定
                            selectableArray[sqrIdx].canSelect[num] = false
                        }
                    }
                    if(kindOfSelectable < KIND_OF_DATA - depth){
                        return retList
                    }
                    sqrIdx ++
                }
            }
        }
        // 設定できる数値のバリエーション分　再帰呼び出しを行
        for(sqrIdx in 0 until NUM_OF_SQR){

        }

        }

    }

    /**
     *     正解リストを作成してsatisfiedGridTblRepository に登録
     *
     */
    public fun createNew(): MutableList<Array<Array<Int>>> {
        // 空の正解リストを生成する
        val tmpArray: Array<Array<Int>> =
            Array(NumbergameData.NUM_OF_ROW) { Array(Number-gameData.NUM_OF_COL) { 0 } }
        // 正解リストを詰めてい行く順番０番に１～９の乱数を設定する
        for ((rowIdx, colArray) in satisfiedSetOrder.withIndex()) {
            for ((colIdx, order) in colArray.withIndex()) {
                if (0 == satisfiedSetOrder[rowIdx][colIdx]) {
                    // 初期値で設定するセルの場合は値は１～９の乱数を設定
                    tmpArray[rowIdx][colIdx] = Random.nextInt(KIND_OF_DATA) + 1
                }
            }
        }
        // 再帰呼び出し(１回目）を行い戻り値を返却
        return findSatisfiedRecursive(data = tmpArray, depth = 1)
    }
    */
}


