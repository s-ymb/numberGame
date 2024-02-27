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
        val seedArray = (1..KIND_OF_DATA).shuffled()

        // 正解リストの値を生成する数字のIndex番号として入替パターンのオフセット分ずらした位置に配置する
        val retArray: Array<IntArray> = Array(NUM_OF_ROW) { IntArray(NUM_OF_COL) { NUM_NOT_SET } }
        for (rowIdx in 0 until NUM_OF_ROW) {
            for (colIdx in 0 until NUM_OF_COL) {
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

    /*
            行・列 入替のパターン毎に移動させる距離をオフセット配列に設定していく
     */


    private fun getOffset(type: Int, direction: Int, pattern: Int,area: Int): Array<IntArray> {
        val retOffset =
            Array(NUM_OF_ROW) { IntArray(NUM_OF_COL) { NUM_NOT_SET } }
        if (type == RotateType.LINE_ROTATE.ordinal) {
            // 行 or 列 入替の場合
            if (direction == RotateDirection.ROW.ordinal) {
                //行入替の場合
                //マスクのパターンを取得
                val maskOffset = getRowMask(pattern = pattern)
                //指定エリアにマスクパターンを反映する
                val areaStartRow = SQR_SIZE * area
                for (maskIdx in 0 until SQR_SIZE) {
                    for (colIdx in 0 until NUM_OF_COL) {
                        retOffset[areaStartRow + maskIdx][colIdx] = maskOffset[maskIdx][colIdx]
                    }
                }
            } else if (direction == RotateDirection.COL.ordinal) {
                //列入替の場合
                // マスクのパターンを取得
                val maskOffset = getColMask(pattern = pattern)
                //指定エリアにマスクパターンを反映する
                val areaStartCol = SQR_SIZE * area
                for (maskIdx in 0 until SQR_SIZE) {
                    for (rowIdx in 0 until NUM_OF_ROW) {
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
                        for (maskIdx in 0 until SQR_SIZE) {
                            for (colIdx in 0 until NUM_OF_COL) {
                                retOffset[maskIdx][colIdx] = 3
                            }
                        }
                        // ２段目のスタート行
                        val startRowIdx = SQR_SIZE
                        //中段エリアに３段上のエリアへの移動を設定
                        for (maskIdx in startRowIdx until startRowIdx + SQR_SIZE) {
                            for (colIdx in 0 until NUM_OF_COL) {
                                retOffset[maskIdx][colIdx] = -3
                            }
                        }
                    }
                    RotatePattern.PATTERN_13.ordinal
                    -> {
                        // 上段のエリアと下段のエリアを入れ替える
                        // 上段エリアに６段下のエリアへの移動を設定
                        for (maskIdx in 0 until SQR_SIZE) {
                            for (colIdx in 0 until NUM_OF_COL) {
                                retOffset[maskIdx][colIdx] = 6
                            }
                        }
                        // 下段エリアに６段上のエリアへの移動を設定
                        // ３段目のスタート行
                        val startRowIdx = SQR_SIZE + SQR_SIZE
                        for (maskIdx in startRowIdx until startRowIdx + SQR_SIZE) {
                            for (colIdx in 0 until NUM_OF_COL) {
                                retOffset[maskIdx][colIdx] = -6
                            }
                        }
                    }

                    RotatePattern.PATTERN_23.ordinal
                    -> {
                        // 上段のエリアと中段のエリアを入れ替える
                        // 上段エリアに３段下のエリアへの移動を設定
                        // ２段目のスタート行
                        val start2RowIdx = SQR_SIZE
                        for (maskIdx in  start2RowIdx  until  start2RowIdx + SQR_SIZE) {
                            for (colIdx in 0 until NUM_OF_COL) {
                                retOffset[maskIdx][colIdx] = 3
                            }
                        }
                        // ３段目のスタート行
                        val start3RowIdx = SQR_SIZE + SQR_SIZE
                        //中段エリアに３段上のエリアへの移動を設定
                        for (maskIdx in start3RowIdx until start3RowIdx + SQR_SIZE) {
                            for (colIdx in 0 until NUM_OF_COL) {
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
                        for (maskIdx in 0 until SQR_SIZE) {
                            for (rowIdx in 0 until NUM_OF_ROW) {
                                retOffset[rowIdx][maskIdx] = 3
                            }
                        }
                        // 中側エリアのスタート列
                        val startColIdx = SQR_SIZE
                        //中段エリアに左側のエリアへの移動を設定
                        for (maskIdx in startColIdx until startColIdx + SQR_SIZE) {
                            for (rowIdx in 0 until NUM_OF_ROW) {
                                retOffset[rowIdx][maskIdx] = -3
                            }
                        }
                    }

                    RotatePattern.PATTERN_13.ordinal
                    -> {
                        // 左側のエリアと右側のエリアを入れ替える
                        // 左側エリアに６行右のエリアへの移動を設定
                        for (maskIdx in 0 until SQR_SIZE) {
                            for (rowIdx in 0 until NUM_OF_ROW) {
                                retOffset[rowIdx][maskIdx] = 6
                            }
                        }
                        // 右側エリアに６行右のエリアへの移動を設定
                        // 右側エリアのスタート列
                        val startColIdx = SQR_SIZE + SQR_SIZE
                        for (maskIdx in startColIdx until startColIdx + SQR_SIZE) {
                            for (rowIdx in 0 until NUM_OF_ROW) {
                                retOffset[rowIdx][maskIdx] = -6
                            }
                        }

                    }

                    RotatePattern.PATTERN_23.ordinal
                    -> {
                        // 中側のエリアと右側のエリアを入れ替える
                        // 中側エリアのスタート列
                        val start2ColIdx = SQR_SIZE
                        //中段エリアに右側のエリアへの移動を設定
                        for (maskIdx in start2ColIdx until start2ColIdx + SQR_SIZE) {
                            for (rowIdx in 0 until NUM_OF_ROW) {
                                retOffset[rowIdx][maskIdx] = 3
                            }
                        }
                        // 右側エリアのスタート列
                        val start3ColIdx = SQR_SIZE + SQR_SIZE
                        //中段エリアに右側のエリアへの移動を設定
                        for (maskIdx in start3ColIdx until start3ColIdx + SQR_SIZE) {
                            for (rowIdx in 0 until NUM_OF_ROW) {
                                retOffset[rowIdx][maskIdx] = -3
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
        val retMask = Array(SQR_SIZE){IntArray(NUM_OF_COL){NUM_NOT_SET}}
        when(pattern) {
            RotatePattern.PATTERN_12.ordinal -> {
                // １行目と２行目の入替の場合、１行目([0])に＋１を２行目([1])にー１を設定
                for (colIdx in 0 until NUM_OF_COL) {
                    retMask[0][colIdx] = 1
                    retMask[1][colIdx] = -1
                }
            }
            RotatePattern.PATTERN_13.ordinal -> {
                // １行目と３行目の入替の場合、１行目([0])に＋2を３行目([2])に-2を設定
                for (colIdx in 0 until NUM_OF_COL) {
                    retMask[0][colIdx] = 2
                    retMask[2][colIdx] = -2
                }
            }
            RotatePattern.PATTERN_23.ordinal -> {
                // ２行目と３行目の入替の場合、２行目([1])に＋1を３行目([2])に-1を設定
                for (colIdx in 0 until NUM_OF_COL) {
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
        val retMask = Array(NUM_OF_ROW){IntArray(SQR_SIZE){NUM_NOT_SET}}
        when(pattern) {
            RotatePattern.PATTERN_12.ordinal -> {
                // １列目と２列目の入替の場合、１列目([0])に＋１を２列目([1])にー１を設定
                for (rowIdx in 0 until NUM_OF_ROW) {
                    retMask[rowIdx][0] = 1
                    retMask[rowIdx][1] = -1
                }
            }
            RotatePattern.PATTERN_13.ordinal -> {
                // １列目と３列目の入替の場合、１列目([0])に＋2を３行列([2])に-2を設定
                for (rowIdx in 0 until NUM_OF_ROW) {
                    retMask[rowIdx][0] = 2
                    retMask[rowIdx][2] = -2
                }
            }
            RotatePattern.PATTERN_23.ordinal -> {
                // ２列目と３列目の入替の場合、２列目([1])に＋1を３列目([2])に-1を設定
                for (rowIdx in 0 until NUM_OF_ROW) {
                    retMask[rowIdx][1]= 1
                    retMask[rowIdx][2] = -1
                }
            }
        }
        return retMask
    }



}


