package com.dausinvestama.eaterly.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class CartDb (
    @PrimaryKey(autoGenerate = false) var id_kantins: Int? = null,

    @ColumnInfo(name = "nama_kantin")
    var nama_kantin: String

)