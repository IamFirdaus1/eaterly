package com.dausinvestama.eaterly.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

class OrderItemDb {

    @Entity
    class OrderItemDb(
        @PrimaryKey(autoGenerate = false)
        var id_order: Int,

        @ColumnInfo(name = "harga")
        var harga: Int,

        @ColumnInfo(name = "jumlah")
        var jumlah: Int,

        @ColumnInfo(name = "nama makanan")
        var nama_makanan: String,

        @ColumnInfo(name = "id_kantin")
        var id_kantin: Int,

        @ColumnInfo(name = "id_jenis")
        var id_jenis: Int,

        @ColumnInfo(name = "nama_kantin")
        var nama_kantin: String,

        @ColumnInfo(name = "gambar_makanan")
        var gambar_makanan: String

    )
}