package com.s_ymb.numbergame.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "SavedCellTbl" ,
    primaryKeys = ["id", "row_id", "col_id"],
    foreignKeys = [
        ForeignKey(
            entity = SavedTbl::class,
            parentColumns = ["id"],
            childColumns = ["id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class SavedCellTbl(
    @ColumnInfo(name = "id")
    val id: Int = 0,                // 親テーブル SavedDataTbl のキー
    @ColumnInfo(name = "row_id")
    val row: Int = 0,               // 行
    @ColumnInfo(name = "col_id")
    val col: Int = 0,               // 列
    @ColumnInfo(name = "num")
    val num: Int = 0,               // 設定値
    @ColumnInfo(name = "init")
    val init: Boolean = false       // 初期設定値？
)
