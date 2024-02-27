package com.s_ymb.numbergame.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SatisfiedGridTbl::class], version = 1, exportSchema = false)
//@TypeConverters(DateConverters::class)
abstract class SatisfiedGridDataBase : RoomDatabase() {
    abstract fun satisfiedGridTblDao(): SatisfiedGridTblDao
    companion object {
        @Volatile
        private var INSTANCE: SatisfiedGridDataBase? = null
        fun getDatabase(context: Context): SatisfiedGridDataBase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SatisfiedGridDataBase::class.java,
                    "satisfiedGrid_DB"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}