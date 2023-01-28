package com.dausinvestama.eaterly.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CartDao {
    @Query("SELECT * FROM cartdb")
    fun getAll(): List<CartDb>

    @Query("SELECT * FROM cartdb WHERE id_kantins = (:id_kantin)")
    fun getbyId(id_kantin: Int): List<CartDb>

    @Insert
    fun InsertAll(vararg kantin: CartDb)

    @Query("DELETE FROM CARTDB")
    fun delete()
}