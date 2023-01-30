package com.dausinvestama.eaterly

import android.content.Context
import androidx.room.*
import com.dausinvestama.eaterly.database.CartItemDAO
import com.dausinvestama.eaterly.database.CartItemDb

@Database(entities = [CartItemDb::class], version = 2)
abstract class AppDatabase: RoomDatabase() {
    abstract fun cartDao(): CartItemDAO

    companion object {
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (instance==null){
                instance = Room.databaseBuilder(context, AppDatabase::class.java, "app-database")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            }
            instance!!.openHelper.writableDatabase; //<<<<< FORCE OPEN
            return instance!!;
        }
    }
}