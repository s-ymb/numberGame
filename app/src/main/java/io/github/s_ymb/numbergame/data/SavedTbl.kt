package io.github.s_ymb.numbergame.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SavedTbl")
data class SavedTbl(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "create_dt")
    val createDt: String = "",
    @ColumnInfo(name = "create_user")
    val createUser: String = "",
)
