package com.dausinvestama.eaterly

import android.content.Context
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dausinvestama.eaterly.database.CartDao
import com.dausinvestama.eaterly.database.CartDb
import com.dausinvestama.eaterly.database.CartItemDb

@Database(entities = [CartDb::class], version = 1)
abstract class CartDatabase(): RoomDatabase() {
    abstract fun outerCartDao(): CartDao

    companion object {
        private var instance: CartDatabase? = null

        fun getInstance(context: Context): CartDatabase {
            if (instance==null){
                instance = Room.databaseBuilder(context, CartDatabase::class.java, "cart-database")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            }
            instance!!.openHelper.writableDatabase; //<<<<< FORCE OPEN
            return instance!!;
        }
    }
}
