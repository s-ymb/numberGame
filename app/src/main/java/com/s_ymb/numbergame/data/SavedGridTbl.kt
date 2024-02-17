package com.s_ymb.numbergame.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class SavedGridTbl(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "gridData")
    val gridData: String = "",
    @ColumnInfo(name = "gridDataIsInit")
    val gridDataIsInit: String = "",
    @ColumnInfo(name = "create_date")
    val createDt: String = "",
    @ColumnInfo(name = "create_user")
    val createUser: String = "",
    )

/*
        SavedGridTbl → SavedGrid
*/
fun SavedGridTbl.toSavedGrid() :SavedGrid {
    val tmp: Array<Array<CellData>> = Array(NumbergameData.NUM_OF_ROW) {Array(NumbergameData.NUM_OF_COL){CellData()}}
    val tmpDataStr = this.gridData
    val tmpDataIsInitStr = this.gridDataIsInit
    if (tmpDataStr != "") {
        for (rowId in 0 until NumbergameData.NUM_OF_ROW) {
            for (colId in 0 until NumbergameData.NUM_OF_COL) {
                val pos = rowId * NumbergameData.NUM_OF_COL + colId
                tmp[rowId][colId].num = tmpDataStr.substring(pos, pos + 1).toInt()
                if(tmpDataIsInitStr.substring(pos, pos + 1).toInt() == 1) {
                    tmp[rowId][colId].init = true
                } else {
                    tmp[rowId][colId].init = false
                }
            }
        }
    }
    return SavedGrid(createDt = this.createDt, createUser = this.createUser, data = tmp)
}


/*
        SavedGrid → SavedGridTbl
*/
fun SavedGrid.toSavedGridTbl(): SavedGridTbl {
    var tmpDataStr :String = ""
    var tmpDataIsInit :String = ""
    for (rowId in 0 until NumbergameData.NUM_OF_ROW) {
        for (colId in 0 until NumbergameData.NUM_OF_COL) {
            tmpDataStr += this.data[rowId][colId].num.toString()
            if (this.data[rowId][colId].init) {
                tmpDataIsInit += "1"
            } else {
                tmpDataIsInit += "0"
            }
        }
    }

    return SavedGridTbl(gridData = tmpDataStr, gridDataIsInit = tmpDataIsInit, createDt = this.createDt, createUser = this.createUser )
}

fun GridData.toSavedGridTbl(createUser: String = "", createDt: String = ""): SavedGridTbl{
    var tmpDataStr :String = ""
    var tmpDataIsInit :String = ""

    for (rowId in 0 until NumbergameData.NUM_OF_ROW) {
        for (colId in 0 until NumbergameData.NUM_OF_COL) {
            tmpDataStr += this.data[rowId][colId].num.toString()
            if (this.data[rowId][colId].init) {
                tmpDataIsInit += "1"
            } else {
                tmpDataIsInit += "0"
            }
        }
    }
    return SavedGridTbl(gridData = tmpDataStr, gridDataIsInit = tmpDataIsInit, createDt = createDt, createUser = createUser )
}

