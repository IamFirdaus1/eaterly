package com.dausinvestama.eaterly.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.google.firebase.firestore.auth.User

@Dao
interface CartItemDAO {
    @Query("SELECT * FROM cartitemdb")
    fun getAll(): List<CartItemDb>

    @Query("SELECT * FROM cartitemdb WHERE id_kantin = (:id_kantins)")
    fun getById(id_kantins: Int): List<CartItemDb>

    @Query("SELECT * FROM cartitemdb WHERE id_kantin IN (:id_kantins)")
    fun loadAllByIds(id_kantins: Int) : List<CartItemDb>

    @Insert
    fun InsertAll(vararg makanan: CartItemDb)

    @Query("DELETE FROM cartitemdb")
    fun delete()
}