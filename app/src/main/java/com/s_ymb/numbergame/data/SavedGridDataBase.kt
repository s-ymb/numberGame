package com.s_ymb.numbergame.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SavedGridTbl::class], version = 1, exportSchema = false)
//@TypeConverters(DateConverters::class)
abstract class SavedGridDataBase : RoomDatabase() {
    abstract fun savedGridTblDao(): SavedGridTblDao
    companion object {
        @Volatile
        private var INSTANCE: SavedGridDataBase? = null
        fun getDatabase(context: Context): SavedGridDataBase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SavedGridDataBase::class.java,
                    "savedGrid_DB"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}
