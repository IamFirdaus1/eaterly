package com.dausinvestama.eaterly.database

import androidx.room.*
import com.google.firebase.firestore.auth.User

@Dao
interface CartItemDAO {
    @Query("SELECT * FROM cartitemdb")
    fun getAll(): List<CartItemDb>

    @Query("SELECT * FROM cartitemdb WHERE id_kantin = (:id_kantins)")
    fun getById(id_kantins: Int): List<CartItemDb>

    @Query("SELECT * FROM cartitemdb WHERE id_makanan = (:id_makanan)")
    fun getBymakanan(id_makanan: Int): List<CartItemDb>

    @Update
    fun UpdateOrder(id_makanan: CartItemDb)

    @Query("SELECT * FROM cartitemdb WHERE id_kantin IN (:id_kantins)")
    fun loadAllByIds(id_kantins: Int) : List<CartItemDb>

    @Insert
    fun InsertAll(vararg makanan: CartItemDb)

    @Query("DELETE FROM cartitemdb")
    fun delete()

    @Delete
    fun deleteid(cartItemDb: CartItemDb)
}