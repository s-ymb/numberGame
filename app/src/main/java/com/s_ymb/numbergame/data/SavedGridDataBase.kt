package com.s_ymb.numbergame.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SavedTbl::class,SavedCellTbl::class], version = 1, exportSchema = false)
//@TypeConverters(DateConverters::class)
abstract class SavedDataBase : RoomDatabase() {
    abstract fun savedCellTblDao(): SavedCellTblDao
    abstract fun savedTblDao(): SavedTblDao
    companion object {
        @Volatile
        private var INSTANCE: SavedDataBase? = null
        fun getDatabase(context: Context): SavedDataBase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SavedDataBase::class.java,
                    "savedGrid_DB"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}
